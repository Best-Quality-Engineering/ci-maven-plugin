package tools.bestquality.maven.ci;

import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedProjectAssert
        extends AbstractAssert<GeneratedProjectAssert, Model> {
    private final ExpandPomAssert originator;
    private final MavenProjectResult project;

    public GeneratedProjectAssert(ExpandPomAssert originator, MavenProjectResult project, Model model) {
        super(model, GeneratedProjectAssert.class);
        this.originator = originator;
        this.project = project;
    }

    public GeneratedProjectAssert revisionIsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("revision", expected);
        return myself;
    }

    public GeneratedProjectAssert revisionIsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("revision");
        return myself;
    }

    public GeneratedProjectAssert revisionIsEmpty() {
        assertThat(actual.getProperties()).containsEntry("revision", "");
        return myself;
    }

    public GeneratedProjectAssert sha1IsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("sha1", expected);
        return myself;
    }

    public GeneratedProjectAssert sha1IsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("sha1");
        return myself;
    }

    public GeneratedProjectAssert sha1IsEmpty() {
        assertThat(actual.getProperties()).containsEntry("sha1", "");
        return myself;
    }

    public GeneratedProjectAssert changelistIsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("changelist", expected);
        return myself;
    }

    public GeneratedProjectAssert changelistIsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("changelist");
        return myself;
    }

    public GeneratedProjectAssert changelistIsEmpty() {
        assertThat(actual.getProperties()).containsEntry("changelist", "");
        return myself;
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
