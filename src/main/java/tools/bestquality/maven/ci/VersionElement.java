package tools.bestquality.maven.ci;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import static tools.bestquality.maven.ci.ArtifactVersions.newVersion;

public enum VersionElement
        implements VersionIncrementer {
    MAJOR() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            return newVersion(
                    current.getMajorVersion() + 1,
                    current.getMinorVersion(),
                    current.getIncrementalVersion(),
                    current.getBuildNumber(),
                    current.getQualifier());
        }
    },
    MINOR() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            return newVersion(
                    current.getMajorVersion(),
                    current.getMinorVersion() + 1,
                    current.getIncrementalVersion(),
                    current.getBuildNumber(),
                    current.getQualifier());
        }
    },
    INCREMENTAL() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            return newVersion(
                    current.getMajorVersion(),
                    current.getMinorVersion(),
                    current.getIncrementalVersion() + 1,
                    current.getBuildNumber(),
                    current.getQualifier());
        }
    },
    BUILD() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            return newVersion(
                    current.getMajorVersion(),
                    current.getMinorVersion(),
                    current.getIncrementalVersion(),
                    current.getBuildNumber() + 1,
                    current.getQualifier());
        }
    },
    AUTO() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            if (current.getBuildNumber() > 0) {
                return BUILD.next(current);
            }
            if (current.getIncrementalVersion() > 0) {
                return INCREMENTAL.next(current);
            }
            if (current.getMinorVersion() > 0) {
                return MINOR.next(current);
            }
            return MAJOR.next(current);
        }
    };

    public abstract ArtifactVersion next(ArtifactVersion current);
}
