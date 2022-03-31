package tools.bestquality.maven.ci


import tools.bestquality.maven.test.MojoSpecification

class NextRevisionMojoTest
        extends MojoSpecification {
    NextRevisionMojo mojo

    def setup() {
        mojo = new NextRevisionMojo()
                .withProject(projectMock)
                .withIncrementor("auto")
                .withOutputDirectory(outputPath.toFile())
                .withFilename("next-revision.txt")
        mojo.setLog(logMock)
    }

    def "should output next revision when executed"() {
        given:
        def spy = Spy(mojo)

        and: "a project with ci properties"
        projectProperties.setProperty("revision", "2.2.1")
        projectProperties.setProperty("sha1", "-22")
        projectProperties.setProperty("changelist", "-SNAPSHOT")

        when:
        spy.execute()

        then:
        1 * spy.outputNextRevision(mojo.next())
    }
}
