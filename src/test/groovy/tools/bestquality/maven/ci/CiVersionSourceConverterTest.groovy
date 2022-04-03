package tools.bestquality.maven.ci

import spock.lang.Specification
import spock.lang.Unroll

import static tools.bestquality.maven.ci.CiVersionSources.MERGE_PROJECT_FIRST
import static tools.bestquality.maven.ci.CiVersionSources.MERGE_SYSTEM_FIRST
import static tools.bestquality.maven.ci.CiVersionSources.PROJECT
import static tools.bestquality.maven.ci.CiVersionSources.SYSTEM

class CiVersionSourceConverterTest
        extends Specification {
    CiVersionSourceConverter converter

    def setup() {
        converter = new CiVersionSourceConverter()
    }

    @Unroll
    def "can convert #type is #expected"() {
        expect:
        converter.canConvert(type) == expected

        where:
        type                  | expected
        CiVersionSource.class | true
        String.class          | false
    }

    @Unroll
    def "should convert #value to #expected"() {
        expect:
        converter.fromString(value) == expected

        where:
        value                 | expected
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
}
