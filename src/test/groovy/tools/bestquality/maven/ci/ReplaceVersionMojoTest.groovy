package tools.bestquality.maven.ci

import org.apache.maven.plugin.MojoExecutionException
import tools.bestquality.io.Content
import tools.bestquality.io.Document
import tools.bestquality.maven.test.MojoSpecification

import java.nio.charset.Charset
import java.nio.file.Path

import static java.nio.charset.StandardCharsets.US_ASCII
import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.newBufferedWriter
import static java.util.regex.Pattern.compile
import static tools.bestquality.maven.ci.CiVersionSources.MERGE_SYSTEM_FIRST

class ReplaceVersionMojoTest
        extends MojoSpecification {
    Path location
    Content contentSpy
    ReplaceVersionMojo mojo

    def setup() {
        location = createTempFile(outputPath,"config-",".yml")
        contentSpy = Spy(new Content())
        mojo = new ReplaceVersionMojo(contentSpy)
                .withProject(projectMock)
                .withSession(sessionMock)
                .withSource(MERGE_SYSTEM_FIRST)
                .withDocument(new Document()
                        .withLocation(location)
                        .withEncoding(US_ASCII)
                        .withPattern(compile("^(version:) .*\$"))
                        .withReplacement("\$1 \${version}"))
        mojo.setLog(logMock)
    }

    def setupDocumentContent(Charset encoding, String content) {
        try (BufferedWriter writer = newBufferedWriter(location, encoding)) {
            writer.append(content)
        }
    }

    def "should replace version in document"() {
        given: "a project with the following system properties"
        systemProperties.setProperty("revision", "2.22.2")

        and: "a yaml document with a version reference"
        setupDocumentContent(US_ASCII, "version: 2.2.2")

        when: "the mojo is executed"
        mojo.execute()

        then: "the version should be replaced"
        location.text == "version: 2.22.2"
    }

    def "should raise an execution exception on runtime exception"() {
        given: "a project with the following system properties"
        systemProperties.setProperty("revision", "2.22.2")

        and: "a yaml document with a version reference"
        setupDocumentContent(US_ASCII, "version: 2.2.2")

        and: "an error will occur when reading the document content"
        def error = new RuntimeException("nope")
        contentSpy.read(location, US_ASCII) >> {
            throw error
        }

        when: "the mojo is executed"
        mojo.execute()

        then: "an exception was thrown"
        thrown(MojoExecutionException)

        and: "the version should not be replaced"
        location.text == "version: 2.2.2"
    }
}
