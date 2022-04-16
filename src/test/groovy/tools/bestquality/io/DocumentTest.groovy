package tools.bestquality.io

import spock.lang.Specification

import java.nio.charset.Charset
import java.nio.file.Path

import static java.nio.file.Files.copy
import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.delete
import static java.nio.file.Files.newBufferedWriter
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

import static java.nio.charset.StandardCharsets.UTF_8
import static java.util.regex.Pattern.compile;

class DocumentTest
        extends Specification {
    Path file
    Content content
    Document document

    def setup() {
        file = createTempFile("test", "-file.txt")
        content = new Content()
        document = new Document()
                .withLocation(file)
                .withEncoding(UTF_8)
    }

    def setupContent(String contents, Charset encoding) {
        try (BufferedWriter writer = newBufferedWriter(file, encoding)) {
            writer.append(contents)
        }
    }

    def setupContent(InputStream contents) {
        copy(contents, file, REPLACE_EXISTING)
    }

    def setupContentFromResource(String resource) {
        setupContent(getClass().getResourceAsStream(resource))
    }

    def cleanup() {
        delete(file)
    }

    def "should replace version in markdown document with multiline expression"() {
        given: "a README.md with multiple version references"
        setupContentFromResource("README-input.md")

        and: "a multiline regular expression"
        document.withPattern(compile("(?sm)(<artifactId>ci-maven-plugin<\\/artifactId>\\s+<version>).*?(<\\/version>)"))
                .withReplacement("\$12.22.2\$2")

        when: "the version is updated"
        document.replace(content)

        then: "all references in the document are updated to the specified version"
        file.text == getClass().getResourceAsStream("README-expected.md").text
    }

    def "should replace version in yaml document"() {
        given: "A config.yml with a single version reference"
        setupContentFromResource("config-input.yml")

        and: "a single line regular expression"
        document.withPattern(compile("^(version:).*\$"))
                .withReplacement("\$1 2.22.2")

        when: "the version is updated"
        document.replace(content)

        then: "all references in the document are updated to the specified version"
        file.text == getClass().getResourceAsStream("config-expected.yml").text
    }

    def "should replace version in xml document"() {
        given: "A pom.xml with a single version reference"
        setupContentFromResource("pom-input.xml")

        and: "a single line regular expression"
        document.withPattern(compile("(<plugin.ci.version>).*(<\\/plugin.ci.version>)"))
                .withReplacement("\$12.22.2\$2")

        when: "the version is updated"
        document.replace(content)

        then: "all references in the document are updated to the specified version"
        file.text == getClass().getResourceAsStream("pom-expected.xml").text
    }

    def "two documents should be equal when all properties match"() {
        given:
        def a = new Document()
                .withLocation(file)
                .withEncoding(UTF_8)
                .withPattern(compile("(.*)"))
                .withReplacement("\$1")
        def b = new Document()
                .withLocation(file)
                .withEncoding(UTF_8)
                .withPattern(compile("(.*)"))
                .withReplacement("\$1")

        expect:
        a == b
        a.location == b.location
        a.encoding == b.encoding
        a.pattern.pattern() == b.pattern.pattern()
        a.replacement == b.replacement


        and:
        a.hashCode() == b.hashCode()
    }

    def "two empty documents should be equal"() {
        given:
        def a = new Document()
        def b = new Document()

        expect:
        a == b
        a.location == b.location
        a.encoding == b.encoding
        a.pattern == b.pattern
        a.replacement == b.replacement

        and:
        a.hashCode() == b.hashCode()
    }
}
