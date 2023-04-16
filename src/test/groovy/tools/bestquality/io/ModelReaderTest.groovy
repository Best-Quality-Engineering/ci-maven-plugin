package tools.bestquality.io

import org.codehaus.plexus.util.xml.pull.XmlPullParserException
import spock.lang.Specification

class ModelReaderTest
        extends Specification {
    ModelReader reader

    def setup() {
        reader = new ModelReader()
    }

    def "should read model from string"() {
        given:
        def content = new String(getClass()
                .getResourceAsStream("/tools/bestquality/maven/ci/pom-expected.xml")
                .readAllBytes())

        when:
        def actual = reader.read(content)

        then:
        actual != null
    }

    def "should raise XmlPullParserException when content invalid"() {
        given:
        def content = "bad"

        when:
        reader.read(content)

        then:
        thrown(XmlPullParserException)
    }
}
