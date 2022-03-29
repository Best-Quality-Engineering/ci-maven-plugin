package tools.bestquality.maven.ci;

import org.apache.maven.artifact.versioning.ArtifactVersion;

@FunctionalInterface
public interface VersionIncrementer {
    ArtifactVersion next(ArtifactVersion current);
}
