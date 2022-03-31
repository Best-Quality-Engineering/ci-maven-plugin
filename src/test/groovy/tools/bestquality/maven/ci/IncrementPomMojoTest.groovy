package tools.bestquality.maven.ci

import groovy.xml.XmlParser
import org.apache.maven.plugin.MojoExecutionException
import spock.lang.Unroll
import tools.bestquality.io.Content
import tools.bestquality.maven.test.MojoSpecification

import static java.lang.String.format
import static java.nio.file.Files.exists
import static java.nio.file.Files.readAllBytes

class IncrementPomMojoTest
        extends MojoSpecification {
    Content contentSpy
    IncrementPomMojo mojo

    @Override
    def setup() {
        contentSpy = Spy(new Content())
        mojo = new IncrementPomMojo(contentSpy)
                .withProject(projectMock)
                .withSession(sessionMock)
                .withSource("merge-system-first")
                .withIncrementor("auto")
                .withOutputDirectory(outputPath.toFile())
                .withFilename("next-revision.txt")
        mojo.setLog(logMock)
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
        1 * logMock.info(format("Next ci version is: %s", expected.toExternalForm()))

        where:
        incrementor | expected
        "auto"      | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "build"     | new CiVersion("2.2.2-3", ".22", "-SNAPSHOT")
        "patch"     | new CiVersion("2.2.3-2", ".22", "-SNAPSHOT")
        "minor"     | new CiVersion("2.3.2-2", ".22", "-SNAPSHOT")
        "major"     | new CiVersion("3.2.2-2", ".22", "-SNAPSHOT")
    }

    def "should raise exception on error reading project pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", "11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        and: "an error to be thrown while reading the pom file"
        def error = new IOException("nope")
        contentSpy.read(pom) >> { throw error }

        when: "the mojo is executed"
        mojo.execute()

        then: "an error message is logged"
        1 * logMock.info("Reading project POM file")
        1 * logMock.error(format("Failure reading project POM file: %s", pom.toAbsolutePath()), error)

        and: "an exception is thrown"
        thrown(MojoExecutionException)

        and: "the next revision file was not written"
        !exists(outputPath.resolve("next-revision.txt"))
    }

    def "should raise exception on error writing the project pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", "11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        and: "an error to be thrown while writing the pom file"
        def error = new IOException("nope")
        contentSpy.write(pom, _ as String) >> { throw error }

        when: "the mojo is executed"
        mojo.execute()

        then: "an error message is logged"
        1 * logMock.info(format("Writing incremented POM file to %s", pom.toAbsolutePath()))
        1 * logMock.error(format("Failure writing incremented POM file: %s", pom.toAbsolutePath()), error)
        _ * logMock._

        and: "an exception is thrown"
        thrown(MojoExecutionException)

        and: "the next revision file was not written"
        !exists(outputPath.resolve("next-revision.txt"))
    }

    def "should increment revision in project pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", ".11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when: "the mojo is executed"
        mojo.execute()

        then: "the version is written to file"
        exists(outputPath.resolve("next-revision.txt"))
        new String(readAllBytes(outputPath.resolve("next-revision.txt"))) == "1.1.2.11-SNAPSHOT"

        and: "the pom revision is incremented"
        def project = new XmlParser()
                .parse(pom.toFile())
        project.properties.revision.text() == "1.1.2"
        project.properties.sha1.text() == ".11"
        project.properties.changelist.text() == "-SNAPSHOT"
    }
}
