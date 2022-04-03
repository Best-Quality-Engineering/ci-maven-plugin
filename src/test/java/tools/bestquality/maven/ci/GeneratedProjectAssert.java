package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedProjectAssert
        extends GeneratedModelAssert<GeneratedProjectAssert> {
    private final ExpandPomAssert originator;
    private final MavenProjectResult project;

    public GeneratedProjectAssert(ExpandPomAssert originator, MavenProjectResult project, Model model) {
        super(model, GeneratedProjectAssert.class);
        this.originator = originator;
        this.project = project;
    }

    public ExpandPomAssert and() {
        return originator;
    }

    public GeneratedProjectAssert artifactIsCorrectlyVersioned() {
        File target = new File(project.getTargetProjectDirectory(), "target");
        String artifact = actual.getArtifactId() + "-" + actual.getVersion() + ".jar";
        File jarFile = new File(target, artifact);
        if (!jarFile.isFile() && !jarFile.canRead()) {
            failWithMessage("Expected artifact %s does not exist", jarFile.getAbsolutePath());
        }
        return myself;
    }

    public GeneratedModuleAssert moduleWasGenerated(String module, String version) {
        Model child = originator.readGeneratedProject(project, module);
        assertThat(child.getVersion())
                .isNull();
        assertThat(child.getParent().getVersion())
                .isEqualTo(version);
        return new GeneratedModuleAssert(this, project, actual, module, child);
    }
}
