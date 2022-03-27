package tools.bestquality.maven.ci

import tools.bestquality.maven.test.MojoSpecification

class ExpandPomMojoTest
        extends MojoSpecification {
    ExpandPomMojo mojo

    def setup() {
        mojo = new ExpandPomMojo()
                .withProject(mockProject)
                .withPropertyResolver(new PropertyResolver(mockProject, mockSession))
    }

    def "should generate ci friendly POM file"() {
        given: "a configured mojo"
        mojo.withOutputDirectory(outputPath.toFile())
                .withCiPomFilename("ci-pom.xml")

        and: "a POM file with all ci friendly properties"
        setupPomFromResource("pom-with-all-ci-properties.xml")

        and: "the ci properties are available as system properties"
        systemProperties.setProperty("revision", "2.2.2")
        systemProperties.setProperty("sha1", "22")
        systemProperties.setProperty("changelist", ".RELEASE")

        when: "the mojo is executed"
        mojo.execute()

        then: "a maven consumable POM is expanded with the specified version"
        def expanded = parser.parse(mojo.ciPomPath().toFile())
        expanded.version.text() == "2.2.2.22.RELEASE"

        and: "the ci properties in the pom file are unchanged"
        expanded.properties.revision.text() == "1.1.1"
        expanded.properties.sha1.text() == "11"
        expanded.properties.changelist.text() == "-SNAPSHOT"

        and: "the expanded POM is set as the project POM file"
        1 * mockProject.setPomFile(mojo.ciPomPath().toFile())
    }
}
