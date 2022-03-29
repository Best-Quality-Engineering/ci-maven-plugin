package tools.bestquality.maven.ci

import tools.bestquality.maven.test.MojoSpecification

import static java.nio.file.Files.exists
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
        exists(outputPath.resolve("next-revision.txt"))
        new String(readAllBytes(outputPath.resolve("next-revision.txt"))) == "2.2.2-22-SNAPSHOT"

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
        exists(outputPath.resolve("next-revision.txt"))
        new String(readAllBytes(outputPath.resolve("next-revision.txt"))) == "2.2.2-22-SNAPSHOT"

        cleanup:
        System.out = original
    }
}
