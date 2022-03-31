package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import tools.bestquality.io.Content;

import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;

@Mojo(name = "increment-pom",
        aggregator = true,
        threadSafe = true,
        defaultPhase = PROCESS_RESOURCES)
public class IncrementPomMojo
        extends IncrementingMojo<IncrementPomMojo> {

    IncrementPomMojo(Content content) {
        super(content);
    }

    public IncrementPomMojo() {
        this(new Content());
    }

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        CiVersion next = next();
        writeIncrementedPom(next.replace(readProjectPom()));
        outputNextRevision(next);
    }

    private String readProjectPom()
            throws MojoExecutionException {
        info("Reading project POM file");
        File pomFile = project.getFile();
        try {
            return content.read(pomFile.toPath());
        } catch (Exception e) {
            error(format("Failure reading project POM file: %s", pomFile.getAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private Path writeIncrementedPom(String pom)
            throws MojoExecutionException {
        Path pomPath = project.getFile().toPath();
        info(format("Writing incremented POM file to %s", pomPath.toAbsolutePath()));
        try {
            content.write(pomPath, pom);
            return pomPath;
        } catch (Exception e) {
            error(format("Failure writing incremented POM file: %s", pomPath.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
