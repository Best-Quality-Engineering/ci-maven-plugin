package tools.bestquality.maven.ci

import tools.bestquality.maven.test.MojoSpecification

class CiPomMojoTest
        extends MojoSpecification {
    CiPomMojo mojo

    def setup() {
        mojo = new CiPomMojo() {
            @Override
            void execute() {
            }
        }
        mojo.setLog(logMock)
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
}
