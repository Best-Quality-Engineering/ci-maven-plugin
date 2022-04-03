package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;

import java.io.File;

public class GeneratedModuleAssert
        extends GeneratedModelAssert<GeneratedModuleAssert> {
    private final GeneratedProjectAssert originator;
    private final MavenProjectResult project;
    private final Model parent;
    private final String module;

    public GeneratedModuleAssert(GeneratedProjectAssert originator, MavenProjectResult project,
                                 Model parent, String module, Model model) {
        super(model, GeneratedModuleAssert.class);
        this.originator = originator;
        this.project = project;
        this.parent = parent;
        this.module = module;
    }

    public GeneratedProjectAssert and() {
        return originator;
    }

    public GeneratedModuleAssert artifactIsCorrectlyVersioned() {
        File moduleDirectory = new File(project.getTargetProjectDirectory(), module);
        File target = new File(moduleDirectory, "target");
        String artifact = actual.getArtifactId() + "-" + parent.getVersion() + ".jar";
        File jarFile = new File(target, artifact);
        if (!jarFile.isFile() && !jarFile.canRead()) {
            failWithMessage("Expected artifact %s does not exist", jarFile.getAbsolutePath());
        }
        return this;
    }
}
