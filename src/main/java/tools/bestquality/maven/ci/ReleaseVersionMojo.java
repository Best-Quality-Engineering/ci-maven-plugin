package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import tools.bestquality.io.Content;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "release-version",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class ReleaseVersionMojo
        extends ExportVersionMojo<ReleaseVersionMojo> {
    @Parameter(defaultValue = "release-version.txt")
    private String filename;

    ReleaseVersionMojo(Content content) {
        super(content);
    }

    public ReleaseVersionMojo() {
        this(new Content());
    }

    public ReleaseVersionMojo withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public void execute()
            throws MojoFailureException, MojoExecutionException {
        CiVersion release = current()
                .release();
        exportVersion(filename, release.toExternalForm());
    }
}
