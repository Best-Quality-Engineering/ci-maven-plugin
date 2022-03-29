package tools.bestquality.maven.ci

import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.ci.VersionIncrementer.version

class VersionIncrementerTest
        extends Specification {

    @Unroll
    def "should produce new version #expected from major:#major minor:#minor incremental:#incremental build:#build qualifier:#qualifier"() {
        given:
        def version = version(major, minor, incremental, build, qualifier)

        when:
        def actual = version.toString()

        then:
        actual == expected

        where:
        major | minor | incremental | build | qualifier   | expected
        2     | 0     | 0           | 0     | null        | "2"
        2     | 2     | 0           | 0     | null        | "2.2"
        2     | 2     | 2           | 0     | null        | "2.2.2"
        2     | 2     | 2           | 2     | null        | "2.2.2-2"
        2     | 2     | 2           | 2     | "SNAPSHOT" | "2.2.2-2-SNAPSHOT"
    }
}
