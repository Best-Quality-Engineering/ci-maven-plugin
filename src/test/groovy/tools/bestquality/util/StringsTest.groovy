package tools.bestquality.util

import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.util.Strings.isBlank
import static tools.bestquality.util.Strings.isNotBlank
import static tools.bestquality.util.Strings.trim

class StringsTest
        extends Specification {

    @Unroll
    def "should indicate blank is #expected when value is #value"() {
        expect:
        isBlank(value) == expected

        where:
        value   | expected
        null    | true
        ""      | true
        "\t"    | true
        "  "    | true
        "bob"   | false
        " bob " | false
    }

    @Unroll
    def "should indicate not blank is #expected when value is #value"() {
        expect:
        isNotBlank(value) == expected

        where:
        value   | expected
        null    | false
        ""      | false
        "\t"    | false
        "  "    | false
        "bob"   | true
        " bob " | true
    }

    @Unroll
    def "should trim to #expected when value is #value"() {
        expect:
        trim(value) == expected

        where:
        value   | expected
        null    | null
        ""      | ""
        "\t"    | ""
        "  "    | ""
        "bob"   | "bob"
        " bob " | "bob"
    }
}
