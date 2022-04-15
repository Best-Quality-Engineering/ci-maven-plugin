package tools.bestquality.maven.util

import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern

import static java.util.regex.Pattern.compile

class PatternConverterTest
        extends Specification {
    PatternConverter converter

    def setup() {
        converter = new PatternConverter()
    }

    @Unroll
    def "can convert #type is #expected"() {
        expect:
        converter.canConvert(type) == expected

        where:
        type          | expected
        Pattern.class | true
        String.class  | false
    }

    @Unroll
    def "should convert #value to #expected"() {
        expect:
        converter.fromString(value).pattern() == expected.pattern()

        where:
        value | expected
        ".*"  | compile(".*")
    }
}
