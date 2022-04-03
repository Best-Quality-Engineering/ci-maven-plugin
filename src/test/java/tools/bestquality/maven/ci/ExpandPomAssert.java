package tools.bestquality.maven.ci;

import com.soebes.itf.extension.assertj.MavenITAssertions;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.soebes.itf.jupiter.maven.ProjectHelper.readProject;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class ExpandPomAssert
        extends AbstractAssert<ExpandPomAssert, MavenExecutionResult> {
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

    public ExpandPomAssert wasSuccessful() {
        isNotNull();
        if (!actual.isSuccessful()) {
            List<String> logs = logs(actual.getMavenLog().getStdout())
                    .map(s -> " [STDOUT] " + s + "\n")
                    .collect(toList());
            failWithMessage("The build was not successful but was <%s> with returnCode:<%s> log file: <%s>",
                    actual.getResult(), actual.getReturnCode(), logs);
        }
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

    static Stream<String> logs(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new IllegalStateException("Exception occured.", e);
        }
    }
}
