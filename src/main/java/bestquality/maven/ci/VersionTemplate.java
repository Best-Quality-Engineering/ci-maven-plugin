package bestquality.maven.ci;

@FunctionalInterface
public interface VersionTemplate {
    String expand(String revision, String sha1, String changelist);

    static VersionTemplate template(String template) {
        return (revision, sha1, changelist) -> template
                .replace("${revision}", revision != null ? revision : "${revision}")
                .replace("${sha1}", sha1 != null ? sha1 : "${sha1}")
                .replace("${changelist}", changelist != null ? changelist : "${changelist}");
    }
}
