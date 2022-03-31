package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import spock.lang.Unroll
import tools.bestquality.io.Content
import tools.bestquality.maven.test.MojoSpecification

import static java.lang.String.format
import static java.nio.file.Files.exists
import static java.nio.file.Files.list
import static java.nio.file.Files.readAllBytes

class IncrementingMojoTest
        extends MojoSpecification {
    Content contentSpy
    IncrementingMojo mojo

    def setup() {
        contentSpy = Spy(new Content())
        mojo = new IncrementingMojo(contentSpy) {
            @Override
            void execute() {
            }
        }
        mojo.setLog(logMock)
        mojo.withProject(projectMock)
                .withIncrementor("auto")
                .withOutputDirectory(outputPath.toFile())
                .withFilename("next-revision.txt")
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
        1 * logMock.info(format("Next revision is: %s", expected.toExternalForm()))

        where:
        incrementor | expected
        "auto"      | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "build"     | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "patch"     | new CiVersion("2.2.3-2", ".22", "-SNAPSHOT")
        "minor"     | new CiVersion("2.3.2-2", ".22", "-SNAPSHOT")
        "major"     | new CiVersion("3.2.2-2", ".22", "-SNAPSHOT")
    }

    def "should raise exception on error writing next version to file"() {
        given: "a project with ci properties"
        projectProperties.setProperty("revision", "2.2.1")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        and: "an error to throw when writing the version to file"
        def error = new RuntimeException("nope")
        contentSpy.write(mojo.nextRevisionPath(), "2.2.2-22-SNAPSHOT") >> { throw error }

        when: "the next revision is output"
        mojo.outputNextRevision(mojo.next())

        then: "an error message is logged"
        1 * logMock.error(format("Failure writing next revision to: %s", mojo.nextRevisionPath()), error)
        _ * logMock._

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

        when: "the next revision is output"
        mojo.outputNextRevision(mojo.next())

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

        when: "the next revision is output"
        mojo.outputNextRevision(mojo.next())

        then:
        captured.toString() == ""

        and:
        exists(mojo.nextRevisionPath())
        new String(readAllBytes(mojo.nextRevisionPath())) == "2.2.2-22-SNAPSHOT"

        cleanup:
        System.out = original
    }
}
