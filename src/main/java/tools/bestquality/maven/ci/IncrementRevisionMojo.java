package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllBytes;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;

@Mojo(name = "increment-revision",
        aggregator = true,
        threadSafe = true,
        defaultPhase = PROCESS_RESOURCES)
public class IncrementRevisionMojo
        extends IncrementingMojo<IncrementRevisionMojo> {

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        writeIncrementedPom(next().replace(readProjectPom()));
    }

    private String readProjectPom()
            throws MojoExecutionException {
        info("Reading project POM file");
        File pomFile = project.getFile();
        try {
            return new String(readAllBytes(pomFile.toPath()), UTF_8);
        } catch (Exception e) {
            error(format("Failure reading project POM file: %s", pomFile.getAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private Path writeIncrementedPom(String content)
            throws MojoExecutionException {
        Path pomPath = project.getFile().toPath();
        info(format("Writing incremented POM file to %s", pomPath.toAbsolutePath()));
        try {
            try (BufferedWriter writer = newBufferedWriter(pomPath, UTF_8)) {
                writer.append(content);
            }
            return pomPath;
        } catch (Exception e) {
            error(format("Failure writing incremented POM file: %s", pomPath.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
