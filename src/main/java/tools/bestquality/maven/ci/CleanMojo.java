package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static org.apache.maven.plugins.annotations.LifecyclePhase.CLEAN;

@Mojo(name = "clean", threadSafe = true, defaultPhase = CLEAN)
public class CleanMojo
        extends CiMojo<CleanMojo> {

    public void execute()
            throws MojoFailureException {
        Path ciPomPath = ciPomPath();
        if (exists(ciPomPath) && !isDirectory(ciPomPath)) {
            info(format("Deleting %s", ciPomPath.toAbsolutePath()));
            try {
                delete(ciPomPath);
            } catch (IOException e) {
                throw new MojoFailureException(format("Failed to delete %s", ciPomPath.toAbsolutePath()));
            }
        }
    }
}