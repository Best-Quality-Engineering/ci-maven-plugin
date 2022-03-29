package tools.bestquality.maven.ci;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.maven.versioning.Incrementer;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;
import static tools.bestquality.maven.ci.CiVersion.versionFrom;
import static tools.bestquality.maven.versioning.ComponentIncrementer.component;

@Mojo(name = "next-revision",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class NextRevisionMojo
        extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "auto", alias = "increment-component")
    private String component;

    @Parameter(property = "forceStdout", alias = "force-stdout", defaultValue = "false")
    private boolean forceStdout;

    @Parameter(defaultValue = "${project.build.directory}/ci")
    private File outputDirectory;

    @Parameter(defaultValue = "next-revision.txt")
    private String filename;


    public NextRevisionMojo withProject(MavenProject project) {
        this.project = project;
        return this;
    }

    public NextRevisionMojo withComponent(String component) {
        this.component = component;
        return this;
    }

    public NextRevisionMojo withForceStdout(boolean forceStdout) {
        this.forceStdout = forceStdout;
        return this;
    }

    public NextRevisionMojo withOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public NextRevisionMojo withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public void execute()
            throws MojoFailureException, MojoExecutionException {
        CiVersion current = versionFrom(project.getProperties());
        CiVersion next = current.next(incrementer());
        getLog().info(format("Next revision is: %s", next.toExternalForm()));
        if (forceStdout) {
            out.print(next.toExternalForm());
            out.flush();
        }
        writeNextRevision(next.toExternalForm());
    }

    private Incrementer incrementer() {
        return component(component);
    }

    Path nextRevisionPath() {
        return outputDirectory.toPath()
                .resolve(filename);
    }

    private void writeNextRevision(String revision)
            throws MojoExecutionException {
        Path path = nextRevisionPath();
        getLog().info(format("Writing next revision to %s", path.toAbsolutePath()));
        try {
            createDirectories(path.getParent());
            try (BufferedWriter writer = newBufferedWriter(path, UTF_8)) {
                writer.append(revision);
            }
        } catch (Exception e) {
            getLog().error(format("Failure writing next revision to: %s", path.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
