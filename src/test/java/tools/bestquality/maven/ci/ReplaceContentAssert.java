package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;

public class ReplaceContentAssert
        extends CiMavenAssert<ReplaceContentAssert> {

    protected ReplaceContentAssert(MavenExecutionResult actual) {
        super(actual, ReplaceContentAssert.class);
    }

    public DocumentAssert contentInDocument(String projectResource, Charset charset)
            throws IOException {
        wasSuccessful();
        MavenProjectResult project = actual.getMavenProjectResult();
        File projectRoot = project.getTargetProjectDirectory();
        Path document = new File(projectRoot, projectResource).toPath();
        return new DocumentAssert(this, new String(readAllBytes(document), charset), charset);
    }
}
