package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Nested;

import java.io.File;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.maven.ProjectHelper.readProject;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@MavenRepository
@MavenJupiterExtension
public class ExpandPomMojoIT {

    public void assertArchiveCreated(MavenProjectResult project, Model generatedModel) {
        File target = new File(project.getTargetProjectDirectory(), "target");
        String artifact = generatedModel.getArtifactId() + "-" + generatedModel.getVersion() + ".jar";
        File jarFile = new File(target, artifact);
        if (!jarFile.isFile() && !jarFile.canRead()) {
            throw new AssertionError(format("Expected artifact %s does not exist", jarFile.getAbsolutePath()));
        }
    }

    public static Model readGeneratedProject(File outputDirectory, String filename) {
        return readProject(new File(outputDirectory, filename));
    }

    public static Model readGeneratedProject(MavenProjectResult project) {
        File outputDirectory = new File(project.getTargetProjectDirectory(), "target/generated-poms");
        return readGeneratedProject(outputDirectory, "pom-ci.xml");
    }

    public static Model readGeneratedProject(MavenProjectResult project, String module) {
        File moduleDirectory = new File(project.getTargetProjectDirectory(), module);
        File outputDirectory = new File(moduleDirectory, "target/generated-poms");
        return readGeneratedProject(outputDirectory, "pom-ci.xml");
    }

    @Nested
    @MavenProject
    class single_module_with_all_properties {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .isSuccessful();

            MavenProjectResult project = execution.getMavenProjectResult();

            // POM file is generated with correct charset
            assertThat(project)
                    .has("target/generated-poms")
                    .withFile("pom-ci.xml")
                    .usingCharset(UTF_8);

            Model model = readGeneratedProject(project);
            assertThat(model.getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
            assertThat(model.getProperties()).containsEntry("revision", "2.22.2");
            assertThat(model.getProperties()).containsEntry("sha1", "");
            assertThat(model.getProperties()).containsEntry("changelist", "-SNAPSHOT");
            assertArchiveCreated(project, model);
        }
    }

    @Nested
    @MavenProject
    class single_module_with_revision_property {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .isSuccessful();

            MavenProjectResult project = execution.getMavenProjectResult();

            // POM file is generated with correct charset
            assertThat(project)
                    .has("target/generated-poms")
                    .withFile("pom-ci.xml")
                    .usingCharset(UTF_8);

            Model model = readGeneratedProject(project);
            assertThat(model.getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
            assertThat(model.getProperties()).containsEntry("revision", "2.22.2-SNAPSHOT");
            assertThat(model.getProperties()).doesNotContainKey("sha1");
            assertThat(model.getProperties()).doesNotContainKey("changelist");
            assertArchiveCreated(project, model);
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_all_properties {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .isSuccessful();

            MavenProjectResult project = execution.getMavenProjectResult();

            // POM file is generated with correct charset
            assertThat(project)
                    .has("target/generated-poms")
                    .withFile("pom-ci.xml")
                    .usingCharset(UTF_8);

            Model parent = readGeneratedProject(project);
            assertThat(parent.getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
            assertThat(parent.getProperties()).containsEntry("revision", "2.22.2");
            assertThat(parent.getProperties()).containsEntry("sha1", "");
            assertThat(parent.getProperties()).containsEntry("changelist", "-SNAPSHOT");

            Model child = readGeneratedProject(project, "child");
            assertThat(child.getVersion())
                    .isNull();
            assertThat(child.getParent().getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_revision_property {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .isSuccessful();

            MavenProjectResult project = execution.getMavenProjectResult();

            // POM file is generated with correct charset
            assertThat(project)
                    .has("target/generated-poms")
                    .withFile("pom-ci.xml")
                    .usingCharset(UTF_8);

            Model parent = readGeneratedProject(project);
            assertThat(parent.getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
            assertThat(parent.getProperties()).containsEntry("revision", "2.22.2-SNAPSHOT");
            assertThat(parent.getProperties()).doesNotContainKey("sha1");
            assertThat(parent.getProperties()).doesNotContainKey("changelist");

            Model child = readGeneratedProject(project, "child");
            assertThat(child.getVersion())
                    .isNull();
            assertThat(child.getParent().getVersion())
                    .isEqualTo("2.22.2-SNAPSHOT");
        }
    }
}
