package tools.bestquality.maven.ci


import tools.bestquality.maven.test.MojoSpecification

class CiMojoTest
        extends MojoSpecification {
    CiMojo mojo

    def setup() {
        mojo = new CiMojo() {
            @Override
            void execute() {
            }
        }
        mojo.setLog(mockLog)
    }

    def "should log info message"() {
        when:
        mojo.info("message")

        then:
        1 * mockLog.info("message")
    }

    def "should log warn message"() {
        when:
        mojo.warn("message")

        then:
        1 * mockLog.warn("message")
    }

    def "should log error message"() {
        given:
        def error = new Exception("nope")

        when:
        mojo.error("message", error)

        then:
        1 * mockLog.error("message", error)
    }
}
