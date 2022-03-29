package tools.bestquality.maven.ci


import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.ci.VersionElement.AUTO
import static tools.bestquality.maven.ci.VersionElement.BUILD
import static tools.bestquality.maven.ci.VersionElement.INCREMENTAL
import static tools.bestquality.maven.ci.VersionElement.MAJOR
import static tools.bestquality.maven.ci.VersionElement.MINOR

class CiVersionTest
        extends Specification {
    CiVersion version

    def setup() {
        version = new CiVersion()
    }

    def "should have all nullable components by default"() {
        expect:
        !version.revision().isPresent()
        !version.sha1().isPresent()
        !version.changelist().isPresent()
    }

    @Unroll
    def "should supply missing component from properties"() {
        given:
        def properties = new Properties()
        if (revision) {
            properties.setProperty("revision", revision)
        }
        if (sha1) {
            properties.setProperty("sha1", sha1)
        }
        if (changelist) {
            properties.setProperty("changelist", changelist)
        }

        when:
        def actual = current.withMissingFrom(properties)

        then:
        actual == current
        actual == expected

        where:
        current                         | revision | sha1 | changelist | expected
        new CiVersion("1", "2", "3")    | "4"      | "5"  | "6"        | new CiVersion("1", "2", "3")
        new CiVersion(null, "2", "3")   | "4"      | "5"  | "6"        | new CiVersion("4", "2", "3")
        new CiVersion("1", null, "3")   | "4"      | "5"  | "6"        | new CiVersion("1", "5", "3")
        new CiVersion("1", "2", null)   | "4"      | "5"  | "6"        | new CiVersion("1", "2", "6")
        new CiVersion(null, null, null) | "4"      | "5"  | "6"        | new CiVersion("4", "5", "6")
        new CiVersion(null, null, null) | null     | null | null       | new CiVersion(null, null, null)
    }

    @Unroll
    def "should expand ci friendly properties in #template with r: #revision s: #sha1 c: #changelist to #expected"() {
        given:
        version.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = version.expand(template)

        then:
        actual == expected

        where:
        template                              | revision | sha1   | changelist  | expected
        "\${revision}"                        | "2.2.2"  | "2222" | "-SNAPSHOT" | "2.2.2"
        "\${revision}"                        | "2.2.2"  | null   | null        | "2.2.2"
        "\${revision}"                        | "2.2.2"  | ""     | ""          | "2.2.2"
        "\${revision}"                        | null     | null   | null        | "\${revision}"
        "\${revision}.\${sha1}\${changelist}" | "2.2.2"  | "2222" | "-SNAPSHOT" | "2.2.2.2222-SNAPSHOT"
        "\${revision}.\${sha1}\${changelist}" | null     | null   | null        | "\${revision}.\${sha1}\${changelist}"
    }

    @Unroll
    def "should replace ci friendly properties in #template with r: #revision s: #sha1 c: #changelist to #expected"() {
        given:
        version.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = version.replace(template)

        then:
        actual == expected

        where:
        template                                | revision | sha1   | changelist  | expected
        "<revision>123</revision>"              | "2.2.2"  | "2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision  >123</revision  >"          | "2.2.2"  | "2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision/>"                           | "2.2.2"  | "2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision />"                          | "2.2.2"  | "2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<sha1>123</sha1>"                      | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1>"
        "<sha1  >123</sha1  >"                  | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1>"
        "<sha1/>"                               | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1>"
        "<sha1 />"                              | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1>"
        "<changelist>.RELEASE</changelist>"     | "2.2.2"  | "2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist  >.RELEASE</changelist  >" | "2.2.2"  | "2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist/>"                         | "2.2.2"  | "2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist />"                        | "2.2.2"  | "2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<revision/>"                           | null     | "2222" | "-SNAPSHOT" | "<revision/>"
        "<revision/>"                           | ""       | "2222" | "-SNAPSHOT" | "<revision/>"
        "<sha1/>"                               | "2.2.2"  | null   | "-SNAPSHOT" | "<sha1/>"
        "<sha1/>"                               | "2.2.2"  | ""     | "-SNAPSHOT" | "<sha1/>"
        "<changelist/>"                         | "2.2.2"  | "2222" | null        | "<changelist/>"
        "<changelist/>"                         | "2.2.2"  | "2222" | ""          | "<changelist/>"
        "<sha1/><sha1/>"                        | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1><sha1>2222</sha1>"
    }

    @Unroll
    def "should provide string representation when r: #revision and s: #sha1 and c: #changelist"() {
        given:
        version.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = version.toString()

        then:
        actual == expected

        where:
        revision | sha1    | changelist  | expected
        "2.2.2"  | ".2222" | "-SNAPSHOT" | "2.2.2.2222-SNAPSHOT"
        null     | ".2222" | "-SNAPSHOT" | ".2222-SNAPSHOT"
        "2.2.2"  | null    | "-SNAPSHOT" | "2.2.2-SNAPSHOT"
        "2.2.2"  | ".2222" | null        | "2.2.2.2222"
    }

    @Unroll
    def "should compute next version using #element when r: #revision and s: #sha1 and c: #changelist"() {
        given:
        version.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = version.next(element)

        then:
        actual.toString() == expected

        where:
        element     | revision  | sha1    | changelist  | expected
        MAJOR       | "2.2.2"   | ".2222" | "-SNAPSHOT" | "3.2.2.2222-SNAPSHOT"
        MINOR       | "2.2.2"   | ".2222" | "-SNAPSHOT" | "2.3.2.2222-SNAPSHOT"
        INCREMENTAL | "2.2.2"   | ".2222" | "-SNAPSHOT" | "2.2.3.2222-SNAPSHOT"
        BUILD       | "2.2.2-1" | ".2222" | "-SNAPSHOT" | "2.2.2-2.2222-SNAPSHOT"
        AUTO        | "2"       | ".2222" | "-SNAPSHOT" | "3.2222-SNAPSHOT"
        AUTO        | "2.2"     | ".2222" | "-SNAPSHOT" | "2.3.2222-SNAPSHOT"
        AUTO        | "2.2.2"   | ".2222" | "-SNAPSHOT" | "2.2.3.2222-SNAPSHOT"
        AUTO        | "2.2.2-1" | ".2222" | "-SNAPSHOT" | "2.2.2-2.2222-SNAPSHOT"
    }
}
