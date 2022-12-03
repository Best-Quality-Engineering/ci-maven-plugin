package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.Assertions;

public class CiMavenAssertions
        extends Assertions {
    public static ExpandPomAssert assertThatExpanded(MavenExecutionResult actual) {
        return new ExpandPomAssert(actual);
    }

    public static ReplaceContentAssert assertThatReplaced(MavenExecutionResult actual) {
        return new ReplaceContentAssert(actual);
    }
}
