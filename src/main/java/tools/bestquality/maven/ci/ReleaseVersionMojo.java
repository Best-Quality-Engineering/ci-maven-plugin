package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import tools.bestquality.io.Content;

import javax.inject.Inject;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "release-version",
        configurator = "ci-mojo-configurator",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class ReleaseVersionMojo
        extends ExportVersionMojo<ReleaseVersionMojo> {

    /**
     * The filename in the output directory which will contain the exported release version.
     * <p/>
     * This file will not be generated when the <code>scriptable</code> property is
     * <code>true</code>
     */
    @Parameter(property = "filename", defaultValue = "release-version.txt")
    private String filename;

    @Inject
    public ReleaseVersionMojo(Content content) {
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
