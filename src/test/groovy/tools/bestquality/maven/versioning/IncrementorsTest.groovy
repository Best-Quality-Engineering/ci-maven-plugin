package tools.bestquality.maven.versioning


import spock.lang.Specification
import spock.lang.Unroll

import static Incrementors.AUTO
import static Incrementors.BUILD
import static Incrementors.MAJOR
import static Incrementors.MINOR
import static Incrementors.PATCH
import static Incrementors.incrementor
import static tools.bestquality.maven.versioning.Version.parseVersion

class IncrementorsTest
        extends Specification {

    @Unroll
    def "should find item from name when name is #name"() {
        expect:
        incrementor(name) == expected

        where:
        name    | expected
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

    def "should raise exception when item not found from name"() {
        when:
        incrementor("unknown")

        then:
        def thrown = thrown(IllegalArgumentException)
        thrown.message == "No enum constant in tools.bestquality.maven.versioning.Incrementors matching unknown"
    }

    @Unroll
    def "should increment version #incrementor from #current to #expected"() {
        when:
        def actual = incrementor.next(current)

        then:
        actual == expected

        where:
        incrementor | current                 | expected
        MAJOR       | parseVersion("1")       | parseVersion("2")
        MINOR       | parseVersion("2.1")     | parseVersion("2.2")
        PATCH       | parseVersion("2.2.1")   | parseVersion("2.2.2")
        BUILD       | parseVersion("2.2.2-1") | parseVersion("2.2.2-2")
        AUTO        | parseVersion("1")       | parseVersion("2")
        AUTO        | parseVersion("2.1")     | parseVersion("2.2")
        AUTO        | parseVersion("2.2.1")   | parseVersion("2.2.2")
        AUTO        | parseVersion("2.2.2-1") | parseVersion("2.2.2-2")
    }
}
