package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Nested;

import static tools.bestquality.maven.ci.CiMavenAssertions.assertThat;

@MavenRepository
@MavenJupiterExtension
public class ExpandPomMojoIT {

    @Nested
    @MavenProject
    class single_module_with_all_properties {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void revision_system_property(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("22.22.22-SNAPSHOT")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class single_module_with_revision_property {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void revision_system_property(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("22.22.22")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_all_properties {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void revision_system_property(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("22.22.22-SNAPSHOT")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .moduleWasGenerated("child", "22.22.22-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_revision_property {

        @MavenTest
        void no_system_properties(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void revision_system_property(MavenExecutionResult execution) {
            assertThat(execution)
                    .projectWasGenerated("22.22.22")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "22.22.22")
                    .artifactIsCorrectlyVersioned();
        }
    }
}
