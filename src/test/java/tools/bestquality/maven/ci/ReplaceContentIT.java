package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProfile;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Nested;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tools.bestquality.maven.ci.CiMavenAssertions.assertThatReplaced;

/**
 * There is a bug in the ITF library that does not allow specifying an empty string
 * for a @SystemProperty value, it sends empty content as -Dproperty which maven
 * interprets as -Dproperty=true
 */
@MavenRepository
@MavenJupiterExtension
public class ReplaceContentIT {

    @Nested
    @MavenProject
    class single_module_with_multiple_documents {
        @MavenTest
        @MavenGoal("ci:replace-content")
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_replace_all_versions_with_no_system_properties(MavenExecutionResult execution)
                throws IOException {
            Model model = execution.getMavenProjectResult().getModel();
            String pluginVersion = model.getProperties().getProperty("project.plugin.version");
            assertThatReplaced(execution)
                    .contentInDocument("README.md", UTF_8)
                    .isEqualToFormattedTestResource("README-expected-simple.md",
                            "ci-pom", "22.22.22")
                    .and()
                    .contentInDocument("pom.xml", UTF_8)
                    .isEqualToFormattedTestResource("pom-expected.xml",
                            pluginVersion, pluginVersion);
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_parent_only_document {
        @MavenTest
        @MavenGoal("ci:replace-content")
        @MavenProfile("documentation")
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_replace_all_versions_with_no_system_properties(MavenExecutionResult execution)
                throws IOException {
            assertThatReplaced(execution)
                    .contentInDocument("README.md", UTF_8)
                    .isEqualToFormattedTestResource("README-expected-simple.md",
                            "ci-parent-pom", "22.22.22");
        }
    }

    @Nested
    @MavenProject
    class multi_module_with_parent_and_child_document {
        @MavenTest
        @MavenGoal("ci:replace-content")
        @MavenProfile("documentation")
        @SystemProperty(value = "revision", content = "22.22.22")
        void should_replace_all_versions_with_no_system_properties(MavenExecutionResult execution)
                throws IOException {
            assertThatReplaced(execution)
                    .contentInDocument("README.md", UTF_8)
                    .isEqualToFormattedTestResource("README-expected-simple.md",
                            "ci-parent-pom", "22.22.22")
                    .and()
                    .contentInDocument("child/README.md", UTF_8)
                    .isEqualToFormattedTestResource("README-expected-simple.md",
                            "ci-child-pom", "22.22.22");
            ;
        }
    }
}
