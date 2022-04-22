package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import tools.bestquality.io.Content;
import tools.bestquality.io.Document;

import javax.inject.Inject;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.util.Arrays.copyOf;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;

@Mojo(name = "replace-content",
        configurator = "ci-mojo-configurator",
        threadSafe = true,
        defaultPhase = VERIFY)
public class ReplaceContentMojo
        extends CiMojo {
    private final Content content;

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
     *         use <code>${project.version}</code> within the replacement as a placeholder for the version value</li>
     * </ul>
     * <i>Tip: consider using a CDATA section when defining <code>pattern</code> and <code>replacement</code> in XML</i>
     */
    @Parameter(property = "documents")
    private Document[] documents = {};

    @Inject
    public ReplaceContentMojo(Content content) {
        this.content = content;
    }

    public ReplaceContentMojo withDocument(Document document) {
        int length = documents.length;
        documents = copyOf(documents, length + 1);
        documents[length] = document;
        return this;
    }

    @Override
    public void execute()
            throws MojoExecutionException {
        for (Document document : documents) {
            try {
                document.replace(content);
            } catch (Exception e) {
                Path path = document.getLocation().toAbsolutePath();
                error(format("Failure updating version references in %s", path), e);
                throw new MojoExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }
}
