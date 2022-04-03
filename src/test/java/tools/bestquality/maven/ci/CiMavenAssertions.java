package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.Assertions;

public class CiMavenAssertions
        extends Assertions {
    public static ExpandPomAssert assertThat(MavenExecutionResult actual) {
        return new ExpandPomAssert(actual);
    }
}
