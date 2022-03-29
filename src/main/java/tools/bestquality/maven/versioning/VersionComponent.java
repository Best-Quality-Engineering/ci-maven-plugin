package tools.bestquality.maven.versioning;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import static tools.bestquality.maven.versioning.Incrementer.version;

public enum VersionComponent
        implements Incrementer {
    MAJOR() {
        @Override
        public ArtifactVersion next(ArtifactVersion current) {
            return version(
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
            return version(
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
            return version(
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
            return version(
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
