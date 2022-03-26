package bestquality.maven.ci

import bestquality.maven.test.MojoSpecification
import org.apache.maven.plugin.MojoFailureException

class ExpandPomMojoTest
        extends MojoSpecification {
    ExpandPomMojo mojo

    def setup() {
        mojo = new ExpandPomMojo()
                .withProject(mockProject)
                .withPropertyResolver(new PropertyResolver(mockProject, mockSession))
    }

    def "should generate ci friendly POM file"() {
        given:
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("ci-pom.xml")

        and:
        setupPom(getClass().getResourceAsStream("pom-with-all-ci-properties.xml"))

        and:
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", "-SNAPSHOT")

        when:
        mojo.execute()

        then:
        notThrown(MojoFailureException)
    }
}
