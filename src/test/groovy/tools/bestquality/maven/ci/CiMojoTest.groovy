package tools.bestquality.maven.ci

import org.apache.maven.model.Model
import org.apache.maven.project.MavenProject
import spock.lang.Unroll
import tools.bestquality.maven.test.MojoSpecification

import static java.nio.charset.StandardCharsets.US_ASCII
import static java.nio.charset.StandardCharsets.UTF_8

class CiMojoTest
        extends MojoSpecification {
    CiMojo mojo

    def setup() {
        mojo = new CiMojo() {
            @Override
            void execute() {
            }
        }
        mojo.setLog(logMock)
    }

    def "should log info message"() {
        when:
        mojo.info("message")

        then:
        1 * logMock.info("message")
    }

    def "should log warn message"() {
        when:
        mojo.warn("message")

        then:
        1 * logMock.warn("message")
    }

    def "should log error message"() {
        given:
        def error = new Exception("nope")

        when:
        mojo.error("message", error)

        then:
        1 * logMock.error("message", error)
    }

    @Unroll
    def "should select #expected charset for project when encoding is #encoding"() {
        given:
        def project = Mock(MavenProject) {
            getModel() >> {
                def model = new Model()
                model.setModelEncoding(encoding)
                return model
            }
        }

        when:
        def actual = CiMojo.charset(project);

        then:
        actual == expected

        where:
        encoding   | expected
        "us-ascii" | US_ASCII
        "US-ASCII" | US_ASCII
        "UTF-8"    | UTF_8
        "utf-8"    | UTF_8
    }
}
