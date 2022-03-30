package tools.bestquality.maven.ci;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;

public abstract class CiPomMojo<M extends CiPomMojo<M>>
        extends CiMojo {

    @Parameter(alias = "output-directory", defaultValue = "${project.build.directory}/generated-poms")
    private File outputDirectory;

    @Parameter(alias = "ci-pom-filename", defaultValue = "pom-ci.xml")
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
