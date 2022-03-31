package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.io.Content;

import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.file.Files.createDirectories;
import static tools.bestquality.maven.ci.CiVersion.versionFrom;
import static tools.bestquality.maven.versioning.StandardIncrementor.incrementor;

public abstract class IncrementingMojo<M extends IncrementingMojo<M>>
        extends CiMojo {
    protected final Content content;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(alias = "incrementor", property = "incrementor", defaultValue = "auto")
    private String incrementor;

    @Parameter(alias = "force-stdout", property = "force-stdout", defaultValue = "false")
    private boolean forceStdout;

    @Parameter(defaultValue = "${project.build.directory}/ci")
    private File outputDirectory;

    @Parameter(defaultValue = "next-revision.txt")
    private String filename;

    public IncrementingMojo(Content content) {
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    public M withProject(MavenProject project) {
        this.project = project;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withIncrementor(String incrementor) {
        this.incrementor = incrementor;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withForceStdout(boolean forceStdout) {
        this.forceStdout = forceStdout;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withFilename(String filename) {
        this.filename = filename;
        return (M) this;
    }

    protected CiVersion current() {
        return versionFrom(project.getProperties());
    }

    protected CiVersion next()
            throws MojoFailureException {
        CiVersion current = current();
        CiVersion next = current.next(incrementor(incrementor));
        info(format("Next revision is: %s", next.toExternalForm()));
        return next;
    }

    protected CiVersion outputNextRevision(CiVersion next)
            throws MojoExecutionException {
        if (forceStdout) {
            out.print(next.toExternalForm());
            out.flush();
        }
        writeNextRevision(next.toExternalForm());
        return next;
    }

    Path nextRevisionPath() {
        return outputDirectory.toPath()
                .resolve(filename);
    }

    private void writeNextRevision(String revision)
            throws MojoExecutionException {
        Path path = nextRevisionPath();
        info(format("Writing next revision to %s", path.toAbsolutePath()));
        try {
            createDirectories(path.getParent());
            content.write(path, revision);
        } catch (Exception e) {
            error(format("Failure writing next revision to: %s", path.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
