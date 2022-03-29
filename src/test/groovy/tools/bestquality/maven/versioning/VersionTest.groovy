package tools.bestquality.maven.versioning

import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Objects.equals
import static tools.bestquality.maven.versioning.Version.parseVersion

class VersionTest
        extends Specification {

    def "should copy version"() {
        given:
        def original = parseVersion("2.2.2-2-SNAPSHOT")

        when:
        def copy = original.copy()

        then:
        equals(copy, original)
    }

    def "should increment major version"() {
        given:
        def current = parseVersion("1.2.2-2-SNAPSHOT")

        when:
        def next = current.nextMajor()

        then:
        next.toExternalForm() == "2.2.2-2-SNAPSHOT"
    }

    def "should increment minor version"() {
        given:
        def current = parseVersion("2.1.2-2-SNAPSHOT")

        when:
        def next = current.nextMinor()

        then:
        next.toExternalForm() == "2.2.2-2-SNAPSHOT"
    }

    def "should increment patch version"() {
        given:
        def current = parseVersion("2.2.1-2-SNAPSHOT")

        when:
        def next = current.nextPatch()

        then:
        next.toExternalForm() == "2.2.2-2-SNAPSHOT"
    }

    def "should increment build version"() {
        given:
        def current = parseVersion("2.2.2-1-SNAPSHOT")

        when:
        def next = current.nextBuild()

        then:
        next.toExternalForm() == "2.2.2-2-SNAPSHOT"
    }

    @Unroll
    def "should parse version string #version into and externalize to #external"() {
        when:
        def actual = parseVersion(version)

        then:
        actual.major.orElse(null) == major
        actual.minor.orElse(null) == minor
        actual.patch.orElse(null) == patch
        actual.build.orElse(null) == build
        actual.qualifier.orElse(null) == qualifier
        actual.toExternalForm() == external

        where:
        version                   | major | minor | patch | build           | qualifier       | external
        "1"                       | 1     | null  | null  | null            | null            | "1"
        "1-23"                    | 1     | null  | null  | 23              | null            | "1-23"
        "1-23.heger"              | 1     | null  | null  | 23              | ".heger"        | "1-23.heger"
        "1.2-45"                  | 1     | 2     | null  | 45              | null            | "1.2-45"
        "1.2-45.qual"             | 1     | 2     | null  | 45              | ".qual"         | "1.2-45.qual"
        "1.2-45-qual"             | 1     | 2     | null  | 45              | "-qual"         | "1.2-45-qual"
        "3.2"                     | 3     | 2     | null  | null            | null            | "3.2"
        "5.7.1"                   | 5     | 7     | 1     | null            | null            | "5.7.1"
        "1.9.04-0012"             | 1     | 9     | 4     | 12              | null            | "1.9.04-0012"
        "01.9.04-0012"            | 1     | 9     | 4     | 12              | null            | "01.9.04-0012"
        "20.03.5-22"              | 20    | 3     | 5     | 22              | null            | "20.03.5-22"
        "20.03.5-056"             | 20    | 3     | 5     | 56              | null            | "20.03.5-056"
        "20.4.06.0-SNAPSHOT"      | 20    | 4     | 6     | null            | "0-SNAPSHOT"    | "20.4.06.0-SNAPSHOT"
        "1.2.3-SNAPSHOT"          | 1     | 2     | 3     | null            | "SNAPSHOT"      | "1.2.3-SNAPSHOT"
        "1.2.3-01-SNAPSHOT"       | 1     | 2     | 3     | 1               | "-SNAPSHOT"     | "1.2.3-01-SNAPSHOT"
        "1.2.3-1-SNAPSHOT"        | 1     | 2     | 3     | 1               | "-SNAPSHOT"     | "1.2.3-1-SNAPSHOT"
        "20.03.5.2016060708"      | 20    | 3     | 5     | null            | "2016060708"    | "20.03.5.2016060708"
        "20.03.5-23-2016060708"   | 20    | 3     | 5     | 23              | "-2016060708"   | "20.03.5-23-2016060708"
        "20.03.5-23.2016060708"   | 20    | 3     | 5     | 23              | ".2016060708"   | "20.03.5-23.2016060708"
        "20.03.5-111.anton"       | 20    | 3     | 5     | 111             | ".anton"        | "20.03.5-111.anton"
        "20.03.5.22-SNAPSHOT"     | 20    | 3     | 5     | null            | "22-SNAPSHOT"   | "20.03.5.22-SNAPSHOT"
        "20.03.5.XYZ.345"         | 20    | 3     | 5     | null            | "XYZ.345"       | "20.03.5.XYZ.345"
        "20-88.03.5.XYZ.345"      | 20    | null  | null  | 88              | ".03.5.XYZ.345" | "20-88.03.5.XYZ.345"
        "20.4-88.03.5.XYZ.345"    | 20    | 4     | null  | 88              | ".03.5.XYZ.345" | "20.4-88.03.5.XYZ.345"
        "020.04-88.03.5.XYZ.345"  | 20    | 4     | null  | 88              | ".03.5.XYZ.345" | "020.04-88.03.5.XYZ.345"
        "20.7.12-88.03.5.XYZ.345" | 20    | 7     | 12    | 88              | ".03.5.XYZ.345" | "20.7.12-88.03.5.XYZ.345"
        "20.03.5-111-34-anton"    | 20    | 3     | 5     | 111             | "-34-anton"     | "20.03.5-111-34-anton"
        "20.03.5-111-34.anton"    | 20    | 3     | 5     | 111             | "-34.anton"     | "20.03.5-111-34.anton"
        "junk"                    | null  | null  | null  | null            | "junk"          | "junk"
        "2.3.4-beta_5"            | 2     | 3     | 4     | null            | "beta_5"        | "2.3.4-beta_5"
        "2.3.4.beta_5"            | 2     | 3     | 4     | null            | "beta_5"        | "2.3.4.beta_5"
        "1.2.3-20171002135756"    | 1     | 2     | 3     | 20171002135756l | null            | "1.2.3-20171002135756"
        "-SNAPSHOT"               | null  | null  | null  | null            | "-SNAPSHOT"     | "-SNAPSHOT"
    }
}