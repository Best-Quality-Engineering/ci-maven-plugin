package tools.bestquality.maven.ci

import spock.lang.Specification
import spock.lang.Unroll

import static VersionTemplate.template

class VersionTemplateTest
        extends Specification {

    @Unroll
    def "should expand ci friendly properties in #template with r: #revision s: #sha1 c: #changelist to #expected"() {
        when:
        def actual = template(template).expand(revision, sha1, changelist)

        then:
        actual == expected

        where:
        template                                | revision | sha1   | changelist  | expected
        "\${revision}"                          | "2.2.2"  | "2222" | "-SNAPSHOT" | "2.2.2"
        "\${revision}"                          | "2.2.2"  | null   | null        | "2.2.2"
        "\${revision}"                          | "2.2.2"  | ""     | ""          | "2.2.2"
        "\${revision}"                          | null     | null   | null        | "\${revision}"
        "\${revision}.\${sha1}\${changelist}"   | "2.2.2"  | "2222" | "-SNAPSHOT" | "2.2.2.2222-SNAPSHOT"
        "\${revision}.\${sha1}\${changelist}"   | null     | null   | null        | "\${revision}.\${sha1}\${changelist}"
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
        "<sha1/>"                               | "2.2.2"  | null   | "-SNAPSHOT" | "<sha1/>"
        "<sha1/>"                               | "2.2.2"  | ""     | "-SNAPSHOT" | "<sha1/>"
        "<sha1/><sha1/>"                        | "2.2.2"  | "2222" | "-SNAPSHOT" | "<sha1>2222</sha1><sha1>2222</sha1>"
    }
}
