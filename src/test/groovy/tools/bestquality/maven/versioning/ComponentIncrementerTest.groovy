package tools.bestquality.maven.versioning

import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.versioning.ComponentIncrementer.AUTO
import static tools.bestquality.maven.versioning.ComponentIncrementer.BUILD
import static tools.bestquality.maven.versioning.ComponentIncrementer.INCREMENTAL
import static tools.bestquality.maven.versioning.ComponentIncrementer.MAJOR
import static tools.bestquality.maven.versioning.ComponentIncrementer.MINOR
import static tools.bestquality.maven.versioning.ComponentIncrementer.component

class ComponentIncrementerTest
        extends Specification {

    @Unroll
    def "should find component from name when name is #name"() {
        expect:
        component(name) == expected

        where:
        name          | expected
        "major"       | MAJOR
        "MAJOR"       | MAJOR
        "minor"       | MINOR
        "MINOR"       | MINOR
        "incremental" | INCREMENTAL
        "INCREMENTAL" | INCREMENTAL
        "build"       | BUILD
        "BUILD"       | BUILD
        "auto"        | AUTO
        "AUTO"        | AUTO
    }

    def "should raise exception when not found from name"() {
        when:
        component("unknown")

        then:
        def thrown = thrown(IllegalArgumentException)
        thrown.message == "No enum constant in tools.bestquality.maven.versioning.ComponentIncrementer matching unknown"
    }

    @Unroll
    def "should increment version #component from #current to #expected"() {
        when:
        def actual = component.next(current)

        then:
        actual == expected

        where:
        component   | current                               | expected
        MAJOR       | new DefaultArtifactVersion("1")       | new DefaultArtifactVersion("2")
        MINOR       | new DefaultArtifactVersion("2.1")     | new DefaultArtifactVersion("2.2")
        INCREMENTAL | new DefaultArtifactVersion("2.2.1")   | new DefaultArtifactVersion("2.2.2")
        BUILD       | new DefaultArtifactVersion("2.2.2-1") | new DefaultArtifactVersion("2.2.2-2")
        AUTO        | new DefaultArtifactVersion("1")       | new DefaultArtifactVersion("2")
        AUTO        | new DefaultArtifactVersion("2.1")     | new DefaultArtifactVersion("2.2")
        AUTO        | new DefaultArtifactVersion("2.2.1")   | new DefaultArtifactVersion("2.2.2")
        AUTO        | new DefaultArtifactVersion("2.2.2-1") | new DefaultArtifactVersion("2.2.2-2")
    }
}
