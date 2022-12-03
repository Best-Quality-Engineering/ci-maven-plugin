package tools.bestquality.maven.ci;

import com.soebes.itf.extension.assertj.MavenITAssertions;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;

import java.io.File;
import java.nio.charset.Charset;

import static com.soebes.itf.jupiter.maven.ProjectHelper.readProject;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ExpandPomAssert
        extends CiMavenAssert<ExpandPomAssert> {
    private String outputDirectory = "target/generated-poms";
    private String filename = "pom-ci.xml";
    private Charset charset = UTF_8;

    protected ExpandPomAssert(MavenExecutionResult actual) {
        super(actual, ExpandPomAssert.class);
    }

    public ExpandPomAssert withOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        return myself;
    }

    public ExpandPomAssert withFilename(String filename) {
        this.filename = filename;
        return myself;
    }

    public ExpandPomAssert withCharset(Charset charset) {
        this.charset = charset;
        return myself;
    }

    public GeneratedProjectAssert projectWasGenerated(String expected) {
        wasSuccessful();
        MavenProjectResult project = actual.getMavenProjectResult();
        MavenITAssertions.assertThat(project)
                .has(outputDirectory)
                .withFile(filename)
                .usingCharset(charset);

        Model model = readGeneratedProject(project);
        assertThat(model.getVersion())
                .isEqualTo(expected);
        return new GeneratedProjectAssert(this, project, model);
    }

    Model readGeneratedProject(File outputDirectory, String filename) {
        return readProject(new File(outputDirectory, filename));
    }

    Model readGeneratedProject(MavenProjectResult project) {
        File generatedPomDirectory = new File(project.getTargetProjectDirectory(), outputDirectory);
        return readGeneratedProject(generatedPomDirectory, filename);
    }

    Model readGeneratedProject(MavenProjectResult project, String module) {
        File moduleDirectory = new File(project.getTargetProjectDirectory(), module);
        File generatedPomDirectory = new File(moduleDirectory, outputDirectory);
        return readGeneratedProject(generatedPomDirectory, filename);
    }
}
