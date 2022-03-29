package tools.bestquality.maven.ci;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

public class ArtifactVersions {

    public static ArtifactVersion newVersion(int major, int minor, int incremental, int build, String qualifier) {
        return new DefaultArtifactVersion(version(major, minor, incremental, build, qualifier));
    }

    private static String version(int major, int minor, int incremental, int build, String qualifier) {
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
        return builder.toString();
    }
}
