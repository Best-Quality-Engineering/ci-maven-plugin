package tools.bestquality.maven.ci

import org.apache.maven.execution.MavenSession
import org.apache.maven.project.MavenProject
import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.ci.CiVersionSource.MERGE_PROJECT_FIRST
import static tools.bestquality.maven.ci.CiVersionSource.MERGE_SYSTEM_FIRST
import static tools.bestquality.maven.ci.CiVersionSource.PROJECT
import static tools.bestquality.maven.ci.CiVersionSource.SYSTEM
import static tools.bestquality.maven.ci.CiVersionSource.ciVersionSource

class CiVersionSourceTest
        extends Specification {

    @Unroll
    def "should find item from name when name is #name"() {
        expect:
        ciVersionSource(name) == expected

        where:
        name                  | expected
        "project"             | PROJECT
        "PROJECT"             | PROJECT
        "system"              | SYSTEM
        "SYSTEM"              | SYSTEM
        "MERGE_PROJECT_FIRST" | MERGE_PROJECT_FIRST
        "MERGE-PROJECT-FIRST" | MERGE_PROJECT_FIRST
        "merge_project_first" | MERGE_PROJECT_FIRST
        "merge-project-first" | MERGE_PROJECT_FIRST
        "MERGE_SYSTEM_FIRST"  | MERGE_SYSTEM_FIRST
        "MERGE-SYSTEM-FIRST"  | MERGE_SYSTEM_FIRST
        "merge_system_first"  | MERGE_SYSTEM_FIRST
        "merge-system-first"  | MERGE_SYSTEM_FIRST
    }

    def "should raise exception when item not found from name"() {
        when:
        ciVersionSource("unknown")

        then:
        def thrown = thrown(IllegalArgumentException)
        thrown.message == "No enum constant in tools.bestquality.maven.ci.CiVersionSource matching unknown"
    }

    @Unroll
    def "should source version from #source to #expected"() {
        given: "a project with properties"
        def project = Mock(MavenProject) {
            getProperties() >> {
                def properties = new Properties()
                properties.setProperty("revision", "1.11.1")
                properties.setProperty("changelist", "-SNAPSHOT")
                return properties
            }
        }

        and: "a session with system properties"
        def session = Mock(MavenSession) {
            getSystemProperties() >> {
                def properties = new Properties()
                properties.setProperty("revision", "2.22.2")
                properties.setProperty("sha1", "22")
                properties.setProperty("changelist", ".RELEASE")
                return properties
            }
        }

        when:
        def actual = source.from(project, session)

        then:
        actual == expected

        where:
        source              | expected
        PROJECT             | new CiVersion("1.11.1", null, "-SNAPSHOT")
        SYSTEM              | new CiVersion("2.22.2", "22", ".RELEASE")
        MERGE_SYSTEM_FIRST  | new CiVersion("2.22.2", "22", ".RELEASE")
        MERGE_PROJECT_FIRST | new CiVersion("1.11.1", "22", "-SNAPSHOT")
    }
}
