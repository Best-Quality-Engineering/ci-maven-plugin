package tools.bestquality.maven.ci;

import org.codehaus.plexus.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class CiVersion {
    private Optional<String> revision = ofNullable(null);
    private Optional<String> sha1 = ofNullable(null);
    private Optional<String> changelist = ofNullable(null);

    public CiVersion(String revision, String sha1, String changelist) {
        withRevision(revision);
        withSha1(sha1);
        withChangelist(changelist);
    }

    public CiVersion() {
        this(null, null, null);
    }

    public Optional<String> revision() {
        return revision;
    }

    public CiVersion withRevision(Optional<String> revision) {
        this.revision = revision;
        return this;
    }

    public CiVersion withRevision(String revision) {
        return withRevision(ofNullable(revision));
    }

    public Optional<String> sha1() {
        return sha1;
    }

    public CiVersion withSha1(Optional<String> sha1) {
        this.sha1 = sha1;
        return this;
    }

    public CiVersion withSha1(String sha1) {
        return withSha1(ofNullable(sha1));
    }

    public Optional<String> changelist() {
        return changelist;
    }

    public CiVersion withChangelist(Optional<String> changelist) {
        this.changelist = changelist;
        return this;
    }

    public CiVersion withChangelist(String changelist) {
        return withChangelist(ofNullable(changelist));
    }

    public String expand(String template) {
        return template
                .replace("${revision}", revision
                        .orElse("${revision}"))
                .replace("${sha1}", sha1
                        .orElse("${sha1}"))
                .replace("${changelist}", changelist
                        .orElse("${changelist}"));
    }

    public String replace(String template) {
        if (revision.isPresent()) {
            template = template.replaceAll("<revision\\s*>.*</revision\\s*>|<revision\\s*/>",
                    revision.filter(StringUtils::isNotEmpty)
                            .map(r -> format("<revision>%s</revision>", r))
                            .orElse("<revision/>"));
        }
        if (sha1.isPresent()) {
            template = template.replaceAll("<sha1\\s*>.*</sha1\\s*>|<sha1\\s*/>",
                    sha1.filter(StringUtils::isNotEmpty)
                            .map(s -> format("<sha1>%s</sha1>", s))
                            .orElse("<sha1/>"));
        }
        if (changelist.isPresent()) {
            template = template.replaceAll("<changelist\\s*>.*</changelist\\s*>|<changelist\\s*/>",
                    changelist.filter(StringUtils::isNotEmpty)
                            .map(c -> format("<changelist>%s</changelist>", c))
                            .orElse("<changelist/>"));
        }
        return template;
    }

    public CiVersion withMissingFrom(Properties properties) {
        if (!revision.isPresent() && properties.containsKey("revision")) {
            withRevision(properties.getProperty("revision"));
        }
        if (!sha1.isPresent() && properties.containsKey("sha1")) {
            withSha1(properties.getProperty("sha1"));
        }
        if (!changelist.isPresent() && properties.containsKey("changelist")) {
            withChangelist(properties.getProperty("changelist"));
        }
        return this;
    }

    public CiVersion next() {
        return new CiVersion()
                .withRevision(nextRevision())
                .withSha1(this.sha1)
                .withChangelist(this.changelist);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CiVersion that = (CiVersion) o;
        return Objects.equals(revision, that.revision)
                && Objects.equals(sha1, that.sha1)
                && Objects.equals(changelist, that.changelist);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        revision.ifPresent(buffer::append);
        sha1.ifPresent(buffer::append);
        changelist.ifPresent(buffer::append);
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, sha1, changelist);
    }

    private String nextRevision() {
        return "TODO";
    }

    public static CiVersion versionFrom(Properties properties) {
        return new CiVersion().withMissingFrom(properties);
    }
}
