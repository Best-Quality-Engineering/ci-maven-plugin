package tools.bestquality.maven.versioning;

import static java.lang.String.format;
import static java.util.Arrays.stream;

public enum Incrementors
        implements Incrementor {
    MAJOR() {
        @Override
        public Version next(Version current) {
            return current.nextMajor();
        }
    },
    MINOR() {
        @Override
        public Version next(Version current) {
            return current.nextMinor();
        }
    },
    PATCH() {
        @Override
        public Version next(Version current) {
            return current.nextPatch();
        }
    },
    BUILD() {
        @Override
        public Version next(Version current) {
            return current.nextBuild();
        }
    },
    AUTO() {
        @Override
        public Version next(Version current) {
            if (current.getBuild().isPresent()) {
                return BUILD.next(current);
            }
            if (current.getPatch().isPresent()) {
                return PATCH.next(current);
            }
            if (current.getMinor().isPresent()) {
                return MINOR.next(current);
            }
            return MAJOR.next(current);
        }
    };

    public abstract Version next(Version current);

    public static Incrementor incrementor(String name) {
        return stream(values())
                .filter(item -> item.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        format("No enum constant in %s matching %s",
                                Incrementors.class.getCanonicalName(), name)));
    }
}
