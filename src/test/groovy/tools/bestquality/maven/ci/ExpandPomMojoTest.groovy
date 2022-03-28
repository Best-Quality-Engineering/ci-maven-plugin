package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.maven.test.MojoSpecification

import static java.nio.file.Files.list

class ExpandPomMojoTest
        extends MojoSpecification {
    ExpandPomMojo mojo

    def setup() {
        mojo = new ExpandPomMojo()
                .withProject(mockProject)
                .withSession(mockSession)
        mojo.setLog(mockLog)
    }

    def "should do nothing when no changes detected"() {
        when: "the mojo is executed"
        mojo.execute()

        then: "an info message was logged"
        1 * mockLog.info("No changes detected in expanded POM, retaining current project POM file")

        and: "the pom file was not expanded"
        0 * mockProject.setPomFile(_ as File)
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
        systemProperties.setProperty("changelist", ".RELEASE")

        and: "an error to be thrown"
        def error = new IOException("nope")

        when: "the mojo is executed"
        mojo.execute()

        then: "an error is raised while reading"
        mockProject.getFile() >> Mock(File) {
            toPath() >> { throw error }
            getAbsolutePath() >> "pom.xml"
        }

        and: "an error message is logged"
        1 * mockLog.error("Failure reading project POM file: pom.xml", error)
        _ * mockLog._

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should raise exception on error expanding pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", ".RELEASE")

        and: "an error to be thrown"
        def error = new RuntimeException("nope")

        when: "the mojo is executed"
        mojo.execute()

        then: "the error is thrown when accessing the revision"
        mockSession.getSystemProperties() >> { throw error }

        and: "an error message is logged"
        1 * mockLog.error("Failure expanding template POM file", error)
        _ * mockLog._

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }

    def "should generate ci friendly POM file using system properties"() {
        given: "a configured mojo"
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("pom-ci.xml")

        and: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", ".RELEASE")

        when: "the mojo is executed"
        mojo.execute()

        then: "a maven consumable POM is expanded with the specified version"
        def expanded = parser.parse(mojo.ciPomPath().toFile())
        expanded.version.text() == "2.2.2.22.RELEASE"

        and: "the ci properties in the pom file are updated"
        expanded.properties.revision.text() == "2.2.2"
        expanded.properties.sha1.text() == "22"
        expanded.properties.changelist.text() == ".RELEASE"

        and: "the expanded POM is set as the project POM file"
        1 * mockProject.setPomFile(mojo.ciPomPath().toFile())
    }

    def "should generate ci friendly POM file using project properties"() {
        given: "a configured mojo"
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("pom-ci.xml")

        and: "a POM file with all ci friendly properties"
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
        1 * mockProject.setPomFile(mojo.ciPomPath().toFile())
    }
}
