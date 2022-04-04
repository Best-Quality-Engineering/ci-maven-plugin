package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.io.Content
import tools.bestquality.maven.test.MojoSpecification

import static java.lang.String.format
import static java.nio.charset.StandardCharsets.US_ASCII
import static java.nio.file.Files.exists
import static java.nio.file.Files.list
import static java.nio.file.Files.readAllBytes
import static tools.bestquality.maven.ci.CiVersionSources.MERGE_SYSTEM_FIRST

class ExportVersionMojoTest
        extends MojoSpecification {
    Content contentSpy
    ExportVersionMojo mojo

    def setup() {
        contentSpy = Spy(new Content())
        mojo = new ExportVersionMojo(contentSpy) {
            @Override
            void execute() {
            }
        }
        mojo.setLog(logMock)
        mojo.withProject(projectMock)
                .withSession(sessionMock)
                .withSource(MERGE_SYSTEM_FIRST)
                .withOutputDirectory(outputPath.toFile())
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

    def "should raise exception on error exporting version to file"() {
        given: "a mojo configured to export to a file"
        mojo.withScriptable(false)

        and: "an error to throw when writing the version to file"
        def error = new RuntimeException("nope")
        contentSpy.write(outputPath.resolve("file.txt"), US_ASCII, "2.22.2-22-SNAPSHOT") >> {
            throw error
        }

        when: "the next revision is exported"
        mojo.exportVersion("file.txt", "2.22.2-22-SNAPSHOT")

        then: "an error message is logged"
        1 * logMock.info(format("Exporting version to %s", outputPath.resolve("file.txt")))
        1 * logMock.error(format("Failure exporting version to: %s", outputPath.resolve("file.txt")), error)

        and: "the file was not written"
        !list(outputPath)
                .findFirst()
                .isPresent()

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should export version to standard out"() {
        given: "a mojo configured to write to stdout"
        mojo.withScriptable(true)

        and: "the stdout stream is captured"
        def original = System.out
        def captured = new ByteArrayOutputStream()
        System.out = new PrintStream(captured)

        when: "the next revision is exported"
        mojo.exportVersion("file.txt", "2.22.2-22-SNAPSHOT")

        then: "the version was exported to standard"
        captured.toString() == "2.22.2-22-SNAPSHOT"

        and: "the file was not written"
        !list(outputPath)
                .findFirst()
                .isPresent()

        cleanup:
        System.out = original
    }

    def "should not export version to file"() {
        given: "a mojo configured to write to a file"
        mojo.withScriptable(false)

        and: "the stdout stream is captured"
        def original = System.out
        def captured = new ByteArrayOutputStream()
        System.out = new PrintStream(captured)

        when: "the next revision is exported"
        mojo.exportVersion("file.txt", "2.22.2-22-SNAPSHOT")

        then: "nothing was written to standard out"
        captured.toString() == ""

        and: "the file was written"
        exists(outputPath.resolve("file.txt"))
        new String(readAllBytes(outputPath.resolve("file.txt"))) == "2.22.2-22-SNAPSHOT"

        cleanup:
        System.out = original
    }
}
