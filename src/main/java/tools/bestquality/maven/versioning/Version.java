package tools.bestquality.maven.versioning;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

/**
 * A version class that parses versions according to the Maven Build Helper Plugin but
 * retains its original formatting so that the various components can be incremented
 * and externalized back into a string without breaking the original formatting.
 *
 * @see <a href="https://github.com/mojohaus/build-helper-maven-plugin">Maven Build Helper Plugin</a>
 */
public final class Version {
    private static final String MAJOR_MINOR_PATCH_PATTERN = "^((\\d+)(\\.(\\d+)(\\.(\\d+))?)?)";
    private static final Pattern MAJOR_MINOR_PATCH = compile(MAJOR_MINOR_PATCH_PATTERN);
    private static final Pattern DIGITS = compile(MAJOR_MINOR_PATCH_PATTERN + "(.*)$");
    private static final Pattern BUILD_NUMBER = compile("(((\\-)(\\d+)(.*))?)|(\\.(.*))|(\\-(.*))|(.*)$");

    private Optional<Integer> major;
    private String majorFormatPattern;

    private Optional<Integer> minor;
    private String minorFormatPattern;

    private Optional<Integer> patch;
    private String patchFormatPattern;

    private Optional<Long> build;
    private String buildFormatPattern;

    private Optional<String> qualifier;
    private String qualifierSeparator;

    private Version() {
        major = empty();
        majorFormatPattern = "%d";

        minor = empty();
        minorFormatPattern = "%d";

        patch = empty();
        patchFormatPattern = "%d";

        build = empty();
        buildFormatPattern = "%d";

        qualifier = empty();
        qualifierSeparator = "";
    }

    public Optional<Integer> getMajor() {
        return major;
    }

    public Version nextMajor() {
        Version next = copy();
        major.ifPresent(value -> next.major = of(value + 1));
        return next;
    }

    public Optional<Integer> getMinor() {
        return minor;
    }

    public Version nextMinor() {
        Version next = copy();
        minor.ifPresent(value -> next.minor = of(value + 1));
        return next;
    }

    public Optional<Integer> getPatch() {
        return patch;
    }

    public Version nextPatch() {
        Version next = copy();
        patch.ifPresent(value -> next.patch = of(value + 1));
        return next;
    }

    public Optional<Long> getBuild() {
        return build;
    }

    public Version nextBuild() {
        Version next = copy();
        build.ifPresent(value -> next.build = of(value + 1));
        return next;
    }

    public Optional<String> getQualifier() {
        return qualifier;
    }

    public Version copy() {
        Version version = new Version();
        version.major = major;
        version.majorFormatPattern = majorFormatPattern;
        version.minor = minor;
        version.minorFormatPattern = minorFormatPattern;
        version.patch = patch;
        version.patchFormatPattern = patchFormatPattern;
        version.build = build;
        version.buildFormatPattern = buildFormatPattern;
        version.qualifier = qualifier;
        version.qualifierSeparator = qualifierSeparator;
        return version;
    }

    public String toExternalForm() {
        if (qualifierOnly()) {
            return qualifier.get();
        }
        StringBuilder builder = new StringBuilder();
        major.ifPresent(value -> builder.append(format(majorFormatPattern, value)));
        minor.ifPresent(value -> builder.append(".").append(format(minorFormatPattern, value)));
        patch.ifPresent(value -> builder.append(".").append(format(patchFormatPattern, value)));
        build.ifPresent(value -> builder.append("-").append(format(buildFormatPattern, value)));
        qualifier.ifPresent(value -> builder.append(qualifierSeparator).append(value));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return Objects.equals(major, version.major)
                && Objects.equals(majorFormatPattern, version.majorFormatPattern)
                && Objects.equals(minor, version.minor)
                && Objects.equals(minorFormatPattern, version.minorFormatPattern)
                && Objects.equals(patch, version.patch)
                && Objects.equals(patchFormatPattern, version.patchFormatPattern)
                && Objects.equals(build, version.build)
                && Objects.equals(buildFormatPattern, version.buildFormatPattern)
                && Objects.equals(qualifier, version.qualifier)
                && Objects.equals(qualifierSeparator, version.qualifierSeparator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, majorFormatPattern,
                minor, minorFormatPattern,
                patch, patchFormatPattern,
                build, buildFormatPattern,
                qualifier, qualifierSeparator);
    }

    @Override
    public String toString() {
        return toExternalForm();
    }

    private boolean qualifierOnly() {
        return !major.isPresent()
                && !minor.isPresent()
                && !patch.isPresent()
                && !build.isPresent();
    }

    private void parseMajorMinorPatch(String version) {
        Matcher matcher = MAJOR_MINOR_PATCH.matcher(version);
        if (matcher.matches()) {
            String majorString = matcher.group(2);
            String minorString = matcher.group(4);
            String patchString = matcher.group(6);
            if (majorString != null) {
                major = of(new Integer(majorString));
                majorFormatPattern = "%0" + majorString.length() + "d";
            }
            if (minorString != null) {
                minor = of(new Integer(minorString));
                minorFormatPattern = "%0" + minorString.length() + "d";
            }
            if (patchString != null) {
                patch = of(new Integer(patchString));
                patchFormatPattern = "%0" + patchString.length() + "d";
            }
        }
    }

    private void parseBuildAndQualifier(String buildNumberPart) {
        Matcher matcher = BUILD_NUMBER.matcher(buildNumberPart);
        if (matcher.matches()) {
            String buildString = matcher.group(4);
            String qualifierString = matcher.group(5);
            if (buildString != null) {
                build = of(new Long(buildString));
                buildFormatPattern = "%0" + buildString.length() + "d";
            }
            if (matcher.group(7) != null) {
                qualifierString = matcher.group(7);
            }
            // Starting with "-"
            if (matcher.group(9) != null) {
                qualifierString = matcher.group(9);
            }
            if (qualifierString != null) {
                if (isNotEmpty(qualifierString)) {
                    qualifier = of(qualifierString);
                    if (!qualifierString.startsWith(".") &&
                            !qualifierString.startsWith("-")) {
                        if (buildString == null) {
                            if (buildNumberPart.startsWith(".")) {
                                qualifierSeparator = ".";
                            } else if (buildNumberPart.startsWith("-")) {
                                qualifierSeparator = "-";
                            }
                        } else {
                            qualifierSeparator = "-";
                        }
                    }
                }
            }
        }
    }

    public static Version parseVersion(String version) {
        Version v = new Version();
        Matcher matcherFormatPattern = DIGITS.matcher(version);
        if (matcherFormatPattern.matches()) {
            v.parseMajorMinorPatch(matcherFormatPattern.group(1));
            v.parseBuildAndQualifier(matcherFormatPattern.group(7));
        } else {
            v.qualifier = ofNullable(version);
        }
        return v;
    }
}
