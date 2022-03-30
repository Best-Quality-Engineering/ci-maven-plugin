package tools.bestquality.maven.ci

import spock.lang.Unroll
import tools.bestquality.maven.test.MojoSpecification

import static java.lang.String.format

class IncrementingMojoTest
        extends MojoSpecification {
    IncrementingMojo mojo

    def setup() {
        mojo = new IncrementingMojo() {
            @Override
            void execute() {
            }
        }
        mojo.withProject(mockProject)
        mojo.setLog(mockLog)
    }

    def "should compute current version"() {
        given: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "2.2.2.2")
        projectProperties.setProperty("sha1", ".22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when:
        def actual = mojo.current()

        then:
        actual == new CiVersion("2.2.2.2", ".22", "-SNAPSHOT")
    }

    @Unroll
    def "should compute next version when incrementor when set to #incrementor"() {
        given:
        mojo.withIncrementor(incrementor)

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "2.2.2-2")
        projectProperties.setProperty("sha1", ".22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when:
        def actual = mojo.next()

        then:
        actual == expected

        and:
        1 * mockLog.info(format("Next revision is: %s", expected.toExternalForm()))

        where:
        incrementor | expected
        "auto"      | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "build"     | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "patch"     | new CiVersion("2.2.3-2", ".22", "-SNAPSHOT")
        "minor"     | new CiVersion("2.3.2-2", ".22", "-SNAPSHOT")
        "major"     | new CiVersion("3.2.2-2", ".22", "-SNAPSHOT")
    }
}
