package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Nested;

import static tools.bestquality.maven.ci.CiMavenAssertions.assertThatExpanded;

@MavenRepository
@MavenJupiterExtension
public class CiMavenPluginIT {

    @Nested
    @MavenProject
    class single_module_with_all_properties {

        @MavenTest
        void should_expand_pom_with_no_system_properties(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_expand_pom_with_revision_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("22.22.22-SNAPSHOT")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist", content = "-RELEASE")
        void should_expand_pom_with_changelist_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-RELEASE")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-RELEASE")
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist=")
            // This is a workaround for bug in empty content handling
        void should_expand_pom_with_changelist_system_property_empty(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEmpty()
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class single_module_with_revision_property {

        @MavenTest
        void should_expand_pom_with_no_system_properties(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_expand_pom_with_revision_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("22.22.22")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist", content = "-RELEASE")
        void should_expand_pom_with_changelist_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist=")
        void should_expand_pom_with_changelist_system_property_empty(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_all_properties {

        @MavenTest
        void should_expand_pom_with_no_system_properties(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_expand_pom_with_revision_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("22.22.22-SNAPSHOT")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-SNAPSHOT")
                    .moduleWasGenerated("child", "22.22.22-SNAPSHOT")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist", content = "-RELEASE")
        void should_expand_pom_with_changelist_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-RELEASE")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEqualTo("-RELEASE")
                    .moduleWasGenerated("child", "2.22.2-RELEASE")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist=")
        void should_expand_pom_with_changelist_system_property_empty(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2")
                    .revisionIsEqualTo("2.22.2")
                    .sha1IsEmpty()
                    .changelistIsEmpty()
                    .moduleWasGenerated("child", "2.22.2")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_revision_property {

        @MavenTest
        void should_expand_pom_with_no_system_properties(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_expand_pom_with_revision_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("22.22.22")
                    .revisionIsEqualTo("22.22.22")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "22.22.22")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist", content = "-RELEASE")
        void should_expand_pom_with_changelist_system_property(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }

        @MavenTest
        @SystemProperty(value = "changelist=")
        void should_expand_pom_with_changelist_system_property_empty(MavenExecutionResult execution) {
            assertThatExpanded(execution)
                    .projectWasGenerated("2.22.2-SNAPSHOT")
                    .revisionIsEqualTo("2.22.2-SNAPSHOT")
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .moduleWasGenerated("child", "2.22.2-SNAPSHOT")
                    .revisionIsNotPresent()
                    .sha1IsNotPresent()
                    .changelistIsNotPresent()
                    .artifactIsCorrectlyVersioned();
        }
    }
}
