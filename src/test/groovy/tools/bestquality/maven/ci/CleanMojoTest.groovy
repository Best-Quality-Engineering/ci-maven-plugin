package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoFailureException
import tools.bestquality.function.CheckedConsumer
import tools.bestquality.maven.test.MojoSpecification

import static java.nio.file.Files.createDirectory
import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.delete
import static java.nio.file.Files.exists

class CleanMojoTest
        extends MojoSpecification {
    CleanMojo mojo

    def setup() {
        mojo = new CleanMojo()
        mojo.setLog(mockLog)
    }

    def "should delete ci pom file when exists"() {
        given:
        def file = createTempFile(outputPath, "pom-", "-ci.xml")
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
                .withCiPomFilename("pom-ci.xml")

        when:
        mojo.execute()

        then:
        noExceptionThrown()
    }

    def "should do nothing when output directory is a file"() {
        given:
        def file = createTempFile("pom-", "-ci.xml")
        mojo.withOutputDirectory(file.toFile())
                .withCiPomFilename("")

        when:
        mojo.execute()

        then:
        noExceptionThrown()

        cleanup:
        file.deleteDir()
    }

    def "should do nothing when computed pom file is a directory"() {
        given:
        def file = createDirectory(outputPath.resolve("sub-dir"))
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename(file.fileName.toString())

        when:
        mojo.execute()

        then:
        noExceptionThrown()
    }

    def "should raise exception when deleting ci pom file fails"() {
        given:
        def file = createTempFile(outputPath, "pom-", "-ci.xml")
        def mockDelete = Mock(CheckedConsumer) {
            accept(file) >> { throw new IOException("boom") }
        }
        mojo = new CleanMojo(mockDelete)
                .withOutputDirectory(outputPath.toFile())
                .withCiPomFilename(file.fileName.toString())

        when:
        mojo.execute()

        then:
        thrown(MojoFailureException)

        cleanup:
        delete(file)
    }
}
