package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.io.Content;

import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.Files.createDirectories;
import static tools.bestquality.maven.ci.CiVersionSource.source;

public abstract class ExportVersionMojo<M extends ExportVersionMojo<M>>
        extends CiMojo {
    protected final Content content;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

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
    protected String source;

    /**
     * The directory containing exported version information
     */
    @Parameter(alias = "output-directory", property = "output-directory", defaultValue = "${project.build.directory}/ci")
    protected File outputDirectory;

    /**
     * Indicates that version information should be exported to standard out rather
     * than a file so that it may be directly assigned to a scripting variable.
     */
    @Parameter(alias = "scriptable", property = "scriptable", defaultValue = "true")
    protected boolean scriptable;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    public ExportVersionMojo(Content content) {
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    public M withProject(MavenProject project) {
        this.project = project;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withSession(MavenSession session) {
        this.session = session;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withSource(String source) {
        this.source = source;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withScriptable(boolean scriptable) {
        this.scriptable = scriptable;
        return (M) this;
    }

    protected CiVersion current() {
        CiVersionSource source = source(this.source);
        return source.from(project, session);
    }

    protected void exportVersion(String filename, String version)
            throws MojoExecutionException {
        if (scriptable) {
            out.print(version);
            out.flush();
        } else {
            Path directory = outputDirectory.toPath();
            Path file = directory.resolve(filename);
            info(format("Exporting version to %s", file.toAbsolutePath()));
            try {
                createDirectories(directory);
                content.write(file, US_ASCII, version);
            } catch (Exception e) {
                error(format("Failure exporting version to: %s", file.toAbsolutePath()), e);
                throw new MojoExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }
}
