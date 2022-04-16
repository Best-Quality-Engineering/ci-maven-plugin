package tools.bestquality.io

import spock.lang.Specification

import java.nio.file.Path

import static java.nio.charset.StandardCharsets.UTF_8
import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.delete
import static java.nio.file.Files.newBufferedWriter
import static java.nio.file.Files.readAllBytes

class ContentTest
        extends Specification {
    Path file
    Content content

    def setup() {
        file = createTempFile("test", "-file.txt")
        content = new Content()
    }

    def cleanup() {
        delete(file)
    }

    def "should read content from path"() {
        given:
        def expected = "Hello"
        try (BufferedWriter writer = newBufferedWriter(file, UTF_8)) {
            writer.append(expected);
        }

        when:
        def actual = content.read(file, UTF_8)

        then:
        actual == expected
    }

    def "should write content to path"() {
        given:
        def expected = "Hello"

        when:
        content.write(file, UTF_8, expected)

        then:
        new String(readAllBytes(file), UTF_8) == expected
    }
}
