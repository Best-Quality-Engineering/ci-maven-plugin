package tools.bestquality.maven.ci;

import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class GeneratedModelAssert<SELF extends GeneratedModelAssert<SELF>>
        extends AbstractAssert<SELF, Model> {

    public GeneratedModelAssert(Model model, Class<?> selfType) {
        super(model, selfType);
    }

    public SELF revisionIsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("revision", expected);
        return myself;
    }

    public SELF revisionIsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("revision");
        return myself;
    }

    public SELF revisionIsEmpty() {
        assertThat(actual.getProperties()).containsEntry("revision", "");
        return myself;
    }

    public SELF sha1IsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("sha1", expected);
        return myself;
    }

    public SELF sha1IsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("sha1");
        return myself;
    }

    public SELF sha1IsEmpty() {
        assertThat(actual.getProperties()).containsEntry("sha1", "");
        return myself;
    }

    public SELF changelistIsEqualTo(String expected) {
        assertThat(actual.getProperties()).containsEntry("changelist", expected);
        return myself;
    }

    public SELF changelistIsNotPresent() {
        assertThat(actual.getProperties()).doesNotContainKey("changelist");
        return myself;
    }

    public SELF changelistIsEmpty() {
        assertThat(actual.getProperties()).containsEntry("changelist", "");
        return myself;
    }

    public abstract SELF artifactIsCorrectlyVersioned();
}
