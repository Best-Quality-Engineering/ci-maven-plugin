package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.io.Content;
import tools.bestquality.io.Document;

import javax.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.util.Arrays.copyOf;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;

@Mojo(name = "replace-version",
        configurator = "ci-mojo-configurator",
        threadSafe = true,
        defaultPhase = PROCESS_RESOURCES)
public class ReplaceVersionMojo
        extends CiMojo {
    private final Content content;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * The source of the properties used to compute the CI Version. There are 2
     * primary sources of properties from which the CI version is built:
     * <p/>
     * <ul>
     *     <li>The maven project's properties</li>
     *     <li>The maven session's system properties</li>
     * </ul>
     * <p/>
     * This parameter determines which set of properties is used, there are options
     * for merging the properties as well. The possible values are:
     * <p/>
     * <ul>
     *     <li><code>project</code></li>
     *     <li><code>system</code></li>
     *     <li><code>merge-system-first</code></li>
     *     <li><code>merge-project-first</code></li>
     * </ul>
     */
    @Parameter(alias = "source", property = "source", defaultValue = "merge-system-first")
    private CiVersionSource source;

    /**
     * Specifies the collection of documents with version references that should be replaced.
     * The following properties of the document are required:
     * <p/>
     * <ul>
     *     <li><code>location</code>: The path to the document containing version references to be replaced</li>
     *     <li><code>encoding</code>: The encoding of the document, must be one available
     *          using <code>java.nio.charset.Charset.forName()</code></li>
     *     <li><code>pattern</code>: The regular expression to match; capture groups can
     *          be referenced in the <code>replacement</code></li>
     *     <li><code>replacement</code>: The string used as the replacement for the matched pattern;
     *         use <code>${version}</code> within the replacement as a placeholder for the version value</li>
     * </ul>
     * <i>Tip: consider using a CDATA section when defining <code>pattern</code> and <code>replacement</code> in XML</i>
     */
    @Parameter(property = "documents")
    private Document[] documents = {};

    @Inject
    public ReplaceVersionMojo(Content content) {
        this.content = content;
    }

    public ReplaceVersionMojo withProject(MavenProject project) {
        this.project = project;
        return this;
    }

    @SuppressWarnings("unchecked")
    public ReplaceVersionMojo withSession(MavenSession session) {
        this.session = session;
        return this;
    }

    @SuppressWarnings("unchecked")
    public ReplaceVersionMojo withSource(CiVersionSource source) {
        this.source = source;
        return this;
    }

    public ReplaceVersionMojo withDocument(Document document) {
        int length = documents.length;
        documents = copyOf(documents, length + 1);
        documents[length] = document;
        return this;
    }

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        CiVersion version = current();
        for (Document document : documents) {
            Path path = document.getLocation().toAbsolutePath();
            info(format("Replacing version references in %s", path));
            try {
                document.updateTo(content, version);
            } catch (Exception e) {
                error(format("Failure updating version references in %s", path), e);
                throw new MojoExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }

    private CiVersion current()
            throws MojoFailureException {
        return source.from(project, session);
    }
}
