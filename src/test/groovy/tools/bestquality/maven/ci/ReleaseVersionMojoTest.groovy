package tools.bestquality.maven.ci

import tools.bestquality.maven.test.MojoSpecification

import static tools.bestquality.maven.ci.CiVersionSources.MERGE_SYSTEM_FIRST

class ReleaseVersionMojoTest
        extends MojoSpecification {
    ReleaseVersionMojo mojo

    def setup() {
        mojo = new ReleaseVersionMojo()
                .withProject(projectMock)
                .withSession(sessionMock)
                .withSource(MERGE_SYSTEM_FIRST)
                .withOutputDirectory(outputPath.toFile())
                .withFilename("release-version.txt")
        mojo.setLog(logMock)
    }

    def "should output next revision when executed"() {
        given:
        def spy = Spy(mojo)

        and: "a project with ci properties"
        projectProperties.setProperty("revision", "2.22.2")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when:
        spy.execute()

        then:
        1 * spy.exportVersion("release-version.txt", "2.22.2-22")
    }
}
