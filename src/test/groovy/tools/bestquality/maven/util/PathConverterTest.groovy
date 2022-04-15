package tools.bestquality.maven.util

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths

class PathConverterTest
        extends Specification {
    PathConverter converter

    def setup() {
        converter = new PathConverter()
    }

    @Unroll
    def "can convert #type is #expected"() {
        expect:
        converter.canConvert(type) == expected

        where:
        type         | expected
        Path.class   | true
        String.class | false
    }

    @Unroll
    def "should convert #value to #expected"() {
        expect:
        converter.fromString(value) == expected

        where:
        value | expected
        "."   | Paths.get(".")
    }
}
