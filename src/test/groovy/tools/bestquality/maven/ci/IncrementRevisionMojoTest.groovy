package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.maven.test.MojoSpecification

class IncrementRevisionMojoTest
        extends MojoSpecification {
    IncrementRevisionMojo mojo

    @Override
    def setup() {
        mojo = new IncrementRevisionMojo()
                .withProject(mockProject)
                .withIncrementor("auto")
        mojo.setLog(mockLog)
    }

    def "should raise exception on error reading project pom"() {
        given: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as project properties"
        projectProperties.setProperty("revision", "1.1.1")
        projectProperties.setProperty("sha1", "11")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

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
        1 * mockLog.info("Reading project POM file")
        1 * mockLog.error("Failure reading project POM file: pom.xml", error)

        and: "an exception is thrown"
        thrown(MojoExecutionException)
    }
}
