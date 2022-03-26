package bestquality.maven.ci

import bestquality.maven.test.MojoSpecification

import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.exists

class CleanMojoTest
        extends MojoSpecification {
    CleanMojo mojo

    def setup() {
        mojo = new CleanMojo()
    }

    def "should delete ci pom file when exists"() {
        given:
        def file = createTempFile(outputPath, "ci-", "-pom.xml")
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename(file.fileName.toString())

        when:
        mojo.execute()

        then:
        !exists(file)
    }

    def "should do nothing when ci pom file does not exist"() {
        given:
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("ci-pom.xml")

        when:
        mojo.execute()

        then:
        noExceptionThrown()
    }

    def "should do nothing when output directory is a file"() {
        given:
        def file = createTempFile("ci-", "-pom.xml")
        mojo.withOutputDirectory(file.toFile())
                .withCiPomFilename("")

        when:
        mojo.execute()

        then:
        noExceptionThrown()

        cleanup:
        file.deleteDir()
    }
}
