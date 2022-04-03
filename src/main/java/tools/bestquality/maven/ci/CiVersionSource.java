package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static tools.bestquality.maven.ci.CiVersion.versionFrom;

enum CiVersionSource {
    PROJECT() {
        @Override
        public CiVersion from(MavenProject project, MavenSession session) {
            return versionFrom(project.getProperties());
        }
    },
    SYSTEM() {
        @Override
        public CiVersion from(MavenProject project, MavenSession session) {
            return versionFrom(session.getSystemProperties());
        }
    },
    MERGE_SYSTEM_FIRST() {
        @Override
        public CiVersion from(MavenProject project, MavenSession session) {
            return versionFrom(session.getSystemProperties())
                    .withMissingFrom(project.getProperties());
        }
    },
    MERGE_PROJECT_FIRST() {
        @Override
        public CiVersion from(MavenProject project, MavenSession session) {
            return versionFrom(project.getProperties())
                    .withMissingFrom(session.getSystemProperties());
        }
    };

    public abstract CiVersion from(MavenProject project, MavenSession session);

    public static CiVersionSource source(String name) {
        return stream(values())
                .filter(item -> item.name()
                        .equalsIgnoreCase(name.replace("-", "_")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        format("No enum constant in %s matching %s",
                                CiVersionSource.class.getCanonicalName(), name)));
    }
}
