package tools.bestquality.maven.ci

import org.apache.maven.model.Model
import org.apache.maven.plugin.MojoFailureException
import spock.lang.Specification
import spock.lang.Unroll
import tools.bestquality.maven.versioning.Incrementor
import tools.bestquality.maven.versioning.Version

import static tools.bestquality.maven.versioning.Incrementors.AUTO
import static tools.bestquality.maven.versioning.Incrementors.BUILD
import static tools.bestquality.maven.versioning.Incrementors.MAJOR
import static tools.bestquality.maven.versioning.Incrementors.MINOR
import static tools.bestquality.maven.versioning.Incrementors.PATCH

class CiVersionTest
        extends Specification {
    CiVersion ciVersion

    def setup() {
        ciVersion = new CiVersion()
    }

    def "should have all nullable components by default"() {
        expect:
        !ciVersion.revision().isPresent()
        !ciVersion.sha1().isPresent()
        !ciVersion.changelist().isPresent()
    }

    @Unroll
    def "should equal self when r: #revision s: #sha1 c: #changelist"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def equal = ciVersion.equals(expected)

        then:
        equal

        where:
        revision | sha1 | changelist | expected
        "1"      | "1"  | "1"        | new CiVersion("1", "1", "1")
        null     | "1"  | "1"        | new CiVersion(null, "1", "1")
        "1"      | null | "1"        | new CiVersion("1", null, "1")
        "1"      | "1"  | null       | new CiVersion("1", "1", null)
        null     | null | null       | new CiVersion()
    }

    @Unroll
    def "should hash self to #expected when r: #revision s: #sha1 c: #changelist"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = ciVersion.hashCode()

        then:
        actual == expected

        where:
        revision | sha1 | changelist | expected
        "1"      | "1"  | "1"        | 78448
        null     | "1"  | "1"        | 31359
        "1"      | null | "1"        | 76929
        "1"      | "1"  | null       | 78399
        null     | null | null       | 29791
    }

    @Unroll
    def "should raise exception when property is specified as boolean"() {
        given:
        def properties = new Properties()
        properties.setProperty("revision", revision)
        properties.setProperty("sha1", sha1)
        properties.setProperty("changelist", changelist)

        when:
        ciVersion.withMissingFrom(properties)

        then:
        thrown(MojoFailureException)

        where:
        revision | sha1   | changelist
        "true"   | "5"    | "6"
        "4"      | "true" | "6"
        "4"      | "5"    | "true"
    }

    @Unroll
    def "should supply missing component from properties"() {
        given:
        def properties = new Properties()
        if (revision != null) {
            properties.setProperty("revision", revision)
        }
        if (sha1 != null) {
            properties.setProperty("sha1", sha1)
        }
        if (changelist != null) {
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
        new CiVersion(null, null, null) | ""       | ""   | ""         | new CiVersion("", "", "")
        new CiVersion(null, null, null) | " "      | " "  | " "        | new CiVersion("", "", "")
        new CiVersion(null, null, null) | "\t"     | "\t" | "\t"       | new CiVersion("", "", "")
    }

    @Unroll
    def "should expand ci friendly properties in #template with r: #revision s: #sha1 c: #changelist to #expected"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = ciVersion.expand(template)

        then:
        actual == expected

        where:
        template                             | revision | sha1    | changelist  | expected
        "\${revision}"                       | "2.2.2"  | "-2222" | "-SNAPSHOT" | "2.2.2"
        "\${revision}"                       | "2.2.2"  | null    | null        | "2.2.2"
        "\${revision}"                       | "2.2.2"  | ""      | ""          | "2.2.2"
        "\${revision}"                       | null     | null    | null        | "\${revision}"
        "\${revision}\${sha1}\${changelist}" | "2.2.2"  | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        "\${revision}\${sha1}\${changelist}" | null     | null    | null        | "\${revision}\${sha1}\${changelist}"
    }

    @Unroll
    def "should replace ci friendly properties in #content with r: #revision s: #sha1 c: #changelist to #expected"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        and:
        expected = "\n<properties>\n\t${expected}\n</properties>\n"

        and:
        def element = "\n<properties>\n\t${content}\n</properties>\n"

        when:
        def actual = ciVersion.replace(element)

        then:
        actual == expected

        where:
        content                                 | revision | sha1    | changelist  | expected
        "<revision>123</revision>"              | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision  >123</revision  >"          | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision/>"                           | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<revision />"                          | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<revision>2.2.2</revision>"
        "<sha1>-123</sha1>"                     | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<sha1>-2222</sha1>"
        "<sha1  >-123</sha1  >"                 | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<sha1>-2222</sha1>"
        "<sha1/>"                               | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<sha1>-2222</sha1>"
        "<sha1 />"                              | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<sha1>-2222</sha1>"
        "<changelist>-RELEASE</changelist>"     | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist  >-RELEASE</changelist  >" | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist/>"                         | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<changelist />"                        | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<changelist>-SNAPSHOT</changelist>"
        "<revision/>"                           | null     | "-2222" | "-SNAPSHOT" | "<revision/>"
        "<revision/>"                           | ""       | "-2222" | "-SNAPSHOT" | "<revision/>"
        "<sha1/>"                               | "2.2.2"  | null    | "-SNAPSHOT" | "<sha1/>"
        "<sha1/>"                               | "2.2.2"  | ""      | "-SNAPSHOT" | "<sha1/>"
        "<changelist/>"                         | "2.2.2"  | "-2222" | null        | "<changelist/>"
        "<changelist/>"                         | "2.2.2"  | "-2222" | ""          | "<changelist/>"
        "<sha1/><sha1/>"                        | "2.2.2"  | "-2222" | "-SNAPSHOT" | "<sha1>-2222</sha1><sha1/>"
    }

    @Unroll
    def "should provide externalized representation when r: #revision and s: #sha1 and c: #changelist"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = ciVersion.toExternalForm()

        then:
        actual == expected

        where:
        revision | sha1    | changelist  | expected
        "2.2.2"  | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        null     | "-2222" | "-SNAPSHOT" | "-2222-SNAPSHOT"
        "2.2.2"  | null    | "-SNAPSHOT" | "2.2.2-SNAPSHOT"
        "2.2.2"  | "-2222" | null        | "2.2.2-2222"
    }

    @Unroll
    def "should provide component representation when r: #revision and s: #sha1 and c: #changelist"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = ciVersion.toComponentForm()

        then:
        actual == expected

        where:
        revision | sha1    | changelist  | expected
        "2.2.2"  | "-2222" | "-SNAPSHOT" | "revision:2.2.2 sha1:-2222 changelist:-SNAPSHOT"
        null     | "-2222" | "-SNAPSHOT" | "sha1:-2222 changelist:-SNAPSHOT"
        ""       | "-2222" | "-SNAPSHOT" | "revision: sha1:-2222 changelist:-SNAPSHOT"
        "2.2.2"  | null    | "-SNAPSHOT" | "revision:2.2.2 changelist:-SNAPSHOT"
        "2.2.2"  | ""      | "-SNAPSHOT" | "revision:2.2.2 sha1: changelist:-SNAPSHOT"
        "2.2.2"  | "-2222" | null        | "revision:2.2.2 sha1:-2222"
        "2.2.2"  | "-2222" | ""          | "revision:2.2.2 sha1:-2222 changelist:"
    }

    def "should raise exception on error incrementing revision"() {
        given:
        ciVersion.withRevision("a.b.c")

        and:
        def mockIncrementor = Mock(Incrementor) {
            next(_ as Version) >> { throw new Exception("nope") }
        }

        when:
        ciVersion.next(mockIncrementor)

        then:
        thrown(MojoFailureException)
    }

    def "should raise exception when revision not present"() {
        when:
        ciVersion.next(MAJOR)

        then:
        thrown(MojoFailureException)
    }

    @Unroll
    def "should compute next version using #incrementor when r: #revision and s: #sha1 and c: #changelist"() {
        given:
        ciVersion.withRevision(revision as String)
                .withSha1(sha1 as String)
                .withChangelist(changelist as String)

        when:
        def actual = ciVersion.next(incrementor)

        then:
        actual.toExternalForm() == expected
        actual.toString() == expected

        where:
        incrementor | revision  | sha1    | changelist  | expected
        MAJOR       | "1.2.2"   | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        MINOR       | "2.1.2"   | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        PATCH       | "2.2.1"   | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        BUILD       | "2.2.2-1" | "-2222" | "-SNAPSHOT" | "2.2.2-2-2222-SNAPSHOT"
        AUTO        | "1"       | "-2222" | "-SNAPSHOT" | "2-2222-SNAPSHOT"
        AUTO        | "2.1"     | "-2222" | "-SNAPSHOT" | "2.2-2222-SNAPSHOT"
        AUTO        | "2.2.1"   | "-2222" | "-SNAPSHOT" | "2.2.2-2222-SNAPSHOT"
        AUTO        | "2.2.2-1" | "-2222" | "-SNAPSHOT" | "2.2.2-2-2222-SNAPSHOT"
    }

    @Unroll
    def "should provide release version #expected when #current"() {
        when:
        def actual = current.release()

        then:
        actual == expected

        where:
        current                                      | expected
        new CiVersion("2.2.2", "-22", "-SNAPSHOT")   | new CiVersion("2.2.2", "-22", null)
        new CiVersion("2.2.2", null, "-SNAPSHOT")    | new CiVersion("2.2.2", null, null)
        new CiVersion("2.2.2", "-22", null)          | new CiVersion("2.2.2", "-22", null)
        new CiVersion("2.2.2", null, null)           | new CiVersion("2.2.2", null, null)
        new CiVersion("2.2.2", "-22", "")            | new CiVersion("2.2.2", "-22", "")
        new CiVersion("2.2.2", null, "")             | new CiVersion("2.2.2", null, "")
        new CiVersion("2.2.2-SNAPSHOT", "-22", null) | new CiVersion("2.2.2", "-22", null)
        new CiVersion("2.2.2-SNAPSHOT", null, null)  | new CiVersion("2.2.2", null, null)
        new CiVersion("2.2.2-SNAPSHOT", "-22", "")   | new CiVersion("2.2.2", "-22", "")
        new CiVersion("2.2.2-SNAPSHOT", null, "")    | new CiVersion("2.2.2", null, "")
    }

    @Unroll
    def "should apply ci version to project model when r: #revision and s: #sha1 and c: #changelist"() {
        given: "a version with all components"
        ciVersion.withRevision(revision as String).
                withSha1(sha1 as String)
                .withChangelist(changelist as String)

        and: "a maven model with empty properties"
        def model = new Model()

        when:
        ciVersion.applyTo(model)

        then:
        model.version == version
        model.properties.getProperty("revision") == revision
        model.properties.getProperty("sha1") == sha1
        model.properties.getProperty("changelist") == changelist

        where:
        revision | sha1  | changelist  | version
        "2.22.2" | "-22" | "-SNAPSHOT" | "2.22.2-22-SNAPSHOT"
        null     | "-22" | "-SNAPSHOT" | "-22-SNAPSHOT"
        "2.22.2" | null  | "-SNAPSHOT" | "2.22.2-SNAPSHOT"
        "2.22.2" | "-22" | null        | "2.22.2-22"
        null     | null  | null        | ""
    }
}
