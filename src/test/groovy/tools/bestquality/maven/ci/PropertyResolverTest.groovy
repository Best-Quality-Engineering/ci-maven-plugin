package tools.bestquality.maven.ci

import tools.bestquality.maven.test.MojoSpecification

class PropertyResolverTest
        extends MojoSpecification {
    PropertyResolver resolver

    @Override
    def setup() {
        resolver = new PropertyResolver(mockProject, mockSession)
    }

    def "should resolve from system properties first"() {
        given:
        projectProperties.setProperty("property", "value-1")
        systemProperties.setProperty("property", "value-2")

        when:
        def actual = resolver.resolve("property")

        then:
        actual == "value-2"
    }

    def "should resolve from project properties when not present in system properties"() {
        given:
        projectProperties.setProperty("property", "value-1")

        when:
        def actual = resolver.resolve("property")

        then:
        actual == "value-1"
    }

    def "should resolve to null when not present in system or project properties"() {
        when:
        def actual = resolver.resolve("property")

        then:
        actual == null
    }

    def "should resolve from supplier when not present in system or project properties"() {
        when:
        def actual = resolver.resolve("property", () -> "value")

        then:
        actual == "value"
    }
}
