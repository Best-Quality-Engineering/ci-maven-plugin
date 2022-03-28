package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import tools.bestquality.maven.test.MojoSpecification

class CiPomMojoTest
        extends MojoSpecification {
    CiPomMojo mojo

    def setup() {
        mojo = new CiPomMojo() {
            @Override
            void execute()
                    throws MojoExecutionException, MojoFailureException {

            }
        }
        mojo.setLog(mockLog)
    }

    def "should use output directory"() {
        given:
        mojo.withOutputDirectory(outputPath.toFile())

        when:
        def actual = mojo.getOutputDirectory()

        then:
        actual == outputPath.toFile()
    }

    def "should use ci pom filename"() {
        given:
        mojo.withCiPomFilename("pom-ci.xml")

        when:
        def actual = mojo.getCiPomFilename()

        then:
        actual == "pom-ci.xml"
    }

    def "should log info message"() {
        when:
        mojo.info("message")

        then:
        1 * mockLog.info("message")
    }

    def "should log error message"() {
        given:
        def error = new Exception("boom")

        when:
        mojo.error("message", error)

        then:
        1 * mockLog.error("message", error)
    }

    def "should log warn message"() {
        when:
        mojo.warn("message")

        then:
        1 * mockLog.warn("message")
    }
}
