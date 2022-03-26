package bestquality.maven.ci

import spock.lang.Unroll

import static bestquality.maven.ci.VersionTemplate.template

class VersionTemplateTest
        extends spock.lang.Specification {

    @Unroll
    def "should expand ci friendly properties in #template with r: #revision s: #sha1 c: #changelist to #expected"() {
        when:
        def actual = template(template).expand(revision, sha1, changelist)

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
}
