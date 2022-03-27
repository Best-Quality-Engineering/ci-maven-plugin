package tools.bestquality.maven.ci;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;

public abstract class CiMojo<M extends CiMojo<M>>
        extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-poms")
    private File outputDirectory;

    @Parameter(defaultValue = "ci-pom.xml")
    private String ciPomFilename;


    public File getOutputDirectory() {
        return outputDirectory;
    }

    @SuppressWarnings("unchecked")
    public M withOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return (M) this;
    }

    public String getCiPomFilename() {
        return ciPomFilename;
    }

    @SuppressWarnings("unchecked")
    public M withCiPomFilename(String ciPomFilename) {
        this.ciPomFilename = ciPomFilename;
        return (M) this;
    }

    protected Path ciPomPath() {
        return outputDirectory.toPath()
                .resolve(ciPomFilename);
    }
}
