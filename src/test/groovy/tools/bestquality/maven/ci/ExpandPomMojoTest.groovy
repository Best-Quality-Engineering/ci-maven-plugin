package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.io.Content
import tools.bestquality.maven.test.MojoSpecification

import static java.lang.String.format
import static java.nio.charset.StandardCharsets.UTF_8
import static java.nio.file.Files.list
import static tools.bestquality.maven.ci.CiVersionSources.MERGE_SYSTEM_FIRST

class ExpandPomMojoTest
        extends MojoSpecification {
    Content contentSpy
    ExpandPomMojo mojo

    def setup() {
        contentSpy = Spy(new Content())
        mojo = new ExpandPomMojo(contentSpy)
                .withProject(projectMock)
                .withSession(sessionMock)
                .withSource(MERGE_SYSTEM_FIRST)
                .withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("pom-ci.xml")
        mojo.setLog(logMock)
    }

    def "should do nothing when no changes detected"() {
        when: "the mojo is executed"
        mojo.execute()

        then: "an info message was logged"
        1 * logMock.info("No changes detected in expanded POM, retaining current project POM file")

        and: "the pom file was not expanded"
        0 * projectMock.setPomFile(_ as File)
        !list(outputPath)
                .findFirst()
                .isPresent()
    }

    def "should raise exception on error reading project pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", "-RELEASE")

        and: "an error to be thrown while reading the pom file"
        def error = new IOException("nope")
        contentSpy.read(pom, UTF_8) >> { throw error }

        when: "the mojo is executed"
        mojo.execute()

        and: "an error message is logged"
        1 * logMock.error(format("Failure reading project POM file: %s", pom.toAbsolutePath()), error)
        _ * logMock._

        and: "the pom file was not expanded"
        0 * projectMock.setPomFile(_ as File)
        !list(outputPath)
                .findFirst()
                .isPresent()

        then: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should raise exception on error expanding pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "-22")
        systemProperties.setProperty("changelist", "-RELEASE")

        and: "an error to throw"
        def error = new RuntimeException("nope")
        def versionSpy = Spy(new CiVersion(null, null, null))
        versionSpy.expand(_) >> { throw error }
        mojo.withSource(Mock(CiVersionSource) {
            from(_, _) >> {
                return versionSpy;
            }
        })

        when: "the mojo is executed"
        mojo.execute()

        then: "an error message is logged"
        1 * logMock.error("Failure expanding template POM file", error)
        _ * logMock._

        and: "the pom file was not expanded"
        0 * projectMock.setPomFile(_ as File)
        !list(outputPath)
                .findFirst()
                .isPresent()

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should raise exception on error writing ci pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", "-RELEASE")

        and: "an error thrown when writing the ci pom file"
        def ciPomPath = mojo.ciPomPath()
        def error = new RuntimeException("nope")
        contentSpy.write(ciPomPath, UTF_8, _) >> { throw error }

        when: "the mojo is executed"
        mojo.execute()

        then: "an error message is logged"
        1 * logMock.error(format("Failure writing expanded POM file: %s", ciPomPath.toAbsolutePath()), error)
        _ * logMock._

        and: "the pom file was not expanded"
        0 * projectMock.setPomFile(_ as File)
        !list(outputPath)
                .findFirst()
                .isPresent()

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should generate ci friendly POM file using system properties"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", "-RELEASE")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", "11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when: "the mojo is executed"
        mojo.execute()

        then: "a maven consumable POM is expanded with the specified version"
        def expanded = parser.parse(mojo.ciPomPath().toFile())
        expanded.version.text() == "2.2.2.22-RELEASE"

        and: "the ci properties in the pom file are updated"
        expanded.properties.revision.text() == "2.2.2"
        expanded.properties.sha1.text() == "22"
        expanded.properties.changelist.text() == "-RELEASE"

        and: "the expanded POM is set as the project POM file"
        1 * projectMock.setPomFile(mojo.ciPomPath().toFile())
    }

    def "should generate ci friendly POM file using project properties"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", "11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when: "the mojo is executed"
        mojo.execute()

        then: "a maven consumable POM is expanded with the specified version"
        def expanded = parser.parse(mojo.ciPomPath().toFile())
        expanded.version.text() == "1.1.1.11-SNAPSHOT"

        and: "the ci properties in the pom file are updated"
        expanded.properties.revision.text() == "1.1.1"
        expanded.properties.sha1.text() == "11"
        expanded.properties.changelist.text() == "-SNAPSHOT"

        and: "the expanded POM is set as the project POM file"
        1 * projectMock.setPomFile(mojo.ciPomPath().toFile())
    }
}
