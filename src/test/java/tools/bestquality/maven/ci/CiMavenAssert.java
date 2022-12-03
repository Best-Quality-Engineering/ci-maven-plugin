package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.AbstractAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CiMavenAssert<A extends CiMavenAssert<A>>
        extends AbstractAssert<A, MavenExecutionResult> {

    protected CiMavenAssert(MavenExecutionResult mavenExecutionResult, Class<?> selfType) {
        super(mavenExecutionResult, selfType);
    }

    public A wasSuccessful() {
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

    static Stream<String> logs(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new IllegalStateException("Exception occurred.", e);
        }
    }
}
