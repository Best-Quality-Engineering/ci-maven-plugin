package tools.bestquality.maven.util

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.Charset

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.charset.StandardCharsets.US_ASCII;

class CharsetConverterTest
        extends Specification {
    CharsetConverter converter

    def setup() {
        converter = new CharsetConverter()
    }

    @Unroll
    def "can convert #type is #expected"() {
        expect:
        converter.canConvert(type) == expected

        where:
        type          | expected
        Charset.class | true
        String.class  | false
    }

    @Unroll
    def "should convert #value to #expected"() {
        expect:
        converter.fromString(value) == expected

        where:
        value      | expected
        "utf-8"    | UTF_8
        "UTF-8"    | UTF_8
        "US-ASCII" | US_ASCII
        "us-ascii" | US_ASCII
        "ascii"    | US_ASCII
    }
}
