package tools.bestquality.maven.versioning

import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.versioning.Incrementors.AUTO
import static tools.bestquality.maven.versioning.Incrementors.BUILD
import static tools.bestquality.maven.versioning.Incrementors.MAJOR
import static tools.bestquality.maven.versioning.Incrementors.MINOR
import static tools.bestquality.maven.versioning.Incrementors.PATCH

class IncrementorConverterTest
        extends Specification {
    IncrementorConverter converter

    def setup() {
        converter = new IncrementorConverter()
    }

    @Unroll
    def "can convert #type is #expected"() {
        expect:
        converter.canConvert(type) == expected

        where:
        type              | expected
        Incrementor.class | true
        String.class      | false
    }

    @Unroll
    def "should convert #value to #expected"() {
        expect:
        converter.fromString(value) == expected

        where:
        value   | expected
        "major" | MAJOR
        "MAJOR" | MAJOR
        "minor" | MINOR
        "MINOR" | MINOR
        "patch" | PATCH
        "PATCH" | PATCH
        "build" | BUILD
        "BUILD" | BUILD
        "auto"  | AUTO
        "AUTO"  | AUTO
    }
}
