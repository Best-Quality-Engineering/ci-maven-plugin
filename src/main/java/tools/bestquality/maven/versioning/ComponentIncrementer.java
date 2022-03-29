package tools.bestquality.maven.versioning;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static tools.bestquality.maven.versioning.Incrementer.version;

public enum ComponentIncrementer
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

    public static ComponentIncrementer component(String name) {
        return stream(values())
                .filter(item -> item.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        format("No enum constant in %s matching %s",
                                ComponentIncrementer.class.getCanonicalName(), name)));
    }
}
