package tools.bestquality.maven.ci;

import static java.lang.String.format;

@FunctionalInterface
public interface VersionTemplate {
    String expand(String revision, String sha1, String changelist)
            throws Exception;

    static VersionTemplate template(String template) {
        return (revision, sha1, changelist) -> {
            // First expand all ci-friendly property references
            String expanded = template
                    .replace("${revision}", revision != null ? revision : "${revision}")
                    .replace("${sha1}", sha1 != null ? sha1 : "${sha1}")
                    .replace("${changelist}", changelist != null ? changelist : "${changelist}");
            // Next update the property definitions
            if (revision != null) {
                expanded = expanded.replaceAll("<revision\\s*>.*</revision\\s*>|<revision\\s*/>",
                        format("<revision>%s</revision>", revision));
            }
            if (sha1 != null) {
                expanded = expanded.replaceAll("<sha1\\s*>.*</sha1\\s*>|<sha1\\s*/>",
                        format("<sha1>%s</sha1>", sha1));
            }
            if (changelist != null) {
                expanded = expanded.replaceAll("<changelist\\s*>.*</changelist\\s*>|<changelist\\s*/>",
                        format("<changelist>%s</changelist>", changelist));
            }
            return expanded;
        };
    }
}
