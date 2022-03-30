package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "next-revision",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class NextRevisionMojo
        extends IncrementingMojo<NextRevisionMojo> {

    @Parameter(alias = "force-stdout", property = "force-stdout", defaultValue = "false")
    private boolean forceStdout;

    @Parameter(defaultValue = "${project.build.directory}/ci")
    private File outputDirectory;

    @Parameter(defaultValue = "next-revision.txt")
    private String filename;


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
        CiVersion next = next();
        if (forceStdout) {
            out.print(next.toExternalForm());
            out.flush();
        }
        writeNextRevision(next.toExternalForm());
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
            try (BufferedWriter writer = newBufferedWriter(path, UTF_8)) {
                writer.append(revision);
            }
        } catch (Exception e) {
            error(format("Failure writing next revision to: %s", path.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
