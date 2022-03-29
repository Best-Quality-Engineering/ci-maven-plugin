package tools.bestquality.maven.ci;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

@FunctionalInterface
public interface VersionIncrementer {
    ArtifactVersion next(ArtifactVersion current);

    static ArtifactVersion version(int major, int minor, int incremental, int build, String qualifier) {
        StringBuilder builder = new StringBuilder();
        builder.append(major);
        if (minor > 0) {
            builder.append(".").append(minor);
        }
        if (incremental > 0) {
            builder.append(".").append(incremental);
        }
        if (build > 0) {
            builder.append("-").append(build);
        }
        if (isNotEmpty(qualifier)) {
            builder.append("-").append(qualifier);
        }
        return new DefaultArtifactVersion(builder.toString());
    }
}
