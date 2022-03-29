package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.maven.test.MojoSpecification

import java.nio.file.Path

import static java.nio.file.Files.exists
import static java.nio.file.Files.list
import static java.nio.file.Files.readAllBytes

class NextRevisionMojoTest
        extends MojoSpecification {
    NextRevisionMojo mojo

    def setup() {
        mojo = new NextRevisionMojo()
                .withProject(mockProject)
                .withComponent("auto")
                .withOutputDirectory(outputPath.toFile())
                .withFilename("next-revision.txt")
        mojo.setLog(mockLog)
    }

    def "should raise exception on error writing ci pom"() {
        given: "a mojo that will throw an exception when getting the revision file parent path"
        def error = new RuntimeException("nope")
        def spyPath = Mock(Path) {
            toAbsolutePath() >> Mock(Path) {
                toString() >> "next-revision.txt"
            }
            getParent() >> { throw error }
        }
        mojo = new NextRevisionMojo() {
            @Override
            protected Path nextRevisionPath() {
                return spyPath
            }
        }
                .withProject(mockProject)
                .withComponent("auto")
                .withOutputDirectory(outputPath.toFile())
                .withFilename("next-revision.txt")
        mojo.setLog(mockLog)

        and: "a project with ci properties"
        projectProperties.setProperty("revision", "2.2.1")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when: "the mojo is executed"
        mojo.execute()

        then: "an error message is logged"
        1 * mockLog.error("Failure writing next revision to: next-revision.txt", error)
        _ * mockLog._

        and: "the revision file was not written"
        !list(outputPath)
                .findFirst()
                .isPresent()

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should write next revision to standard out"() {
        given: "a mojo configured to write to stdout"
        mojo.withForceStdout(true)

        and: "a project with ci properties"
        projectProperties.setProperty("revision", "2.2.1")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        and: "the stdout stream is captured"
        def original = System.out
        def captured = new ByteArrayOutputStream()
        System.out = new PrintStream(captured)

        when:
        mojo.execute()

        then:
        captured.toString() == "2.2.2-22-SNAPSHOT"

        and:
        exists(mojo.nextRevisionPath())
        new String(readAllBytes(mojo.nextRevisionPath())) == "2.2.2-22-SNAPSHOT"

        cleanup:
        System.out = original
    }

    def "should not write next revision to standard out"() {
        given: "a mojo configured to write to stdout"
        mojo.withForceStdout(false)

        and: "a project with ci properties"
        projectProperties.setProperty("revision", "2.2.1")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        and: "the stdout stream is captured"
        def original = System.out
        def captured = new ByteArrayOutputStream()
        System.out = new PrintStream(captured)

        when:
        mojo.execute()

        then:
        captured.toString() == ""

        and:
        exists(mojo.nextRevisionPath())
        new String(readAllBytes(mojo.nextRevisionPath())) == "2.2.2-22-SNAPSHOT"

        cleanup:
        System.out = original
    }
}
