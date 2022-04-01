package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.junit.jupiter.api.Nested;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

@MavenRepository
@MavenJupiterExtension
public class ExpandPomMojoIT {

    @Nested
    @MavenProject
    class single_project_with_all_ci_properties {

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
        }
    }
}
