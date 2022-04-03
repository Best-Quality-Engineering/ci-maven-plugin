package tools.bestquality.maven.ci;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoFailureException;
import tools.bestquality.maven.versioning.Incrementor;
import tools.bestquality.util.Strings;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static tools.bestquality.maven.versioning.Version.parseVersion;
import static tools.bestquality.util.Strings.trim;

public class CiVersion {
    private Optional<String> revision;
    private Optional<String> sha1;
    private Optional<String> changelist;

    public CiVersion(String revision, String sha1, String changelist) {
        this.revision = ofNullable(revision);
        this.sha1 = ofNullable(sha1);
        this.changelist = ofNullable(changelist);
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

    /**
     * Expands all ci friendly property references to:
     * <ul>
     *     <li>{@code ${revision}}</li>
     *     <li>{@code ${sha1}}</li>
     *     <li>{@code ${changelist}}</li>
     * </ul>
     *
     * @param template The content containing ci friendly property references to expand
     * @return The expanded content with property references resolved
     */
    public String expand(String template) {
        return template
                .replace("${revision}", revision
                        .orElse("${revision}"))
                .replace("${sha1}", sha1
                        .orElse("${sha1}"))
                .replace("${changelist}", changelist
                        .orElse("${changelist}"));
    }

    /**
     * Replaces the values assigned to the ci friendly properties in the {@code &lt;properties/&gt;}
     * element with the current values held in this instance.
     *
     * @param pom The POM content to update
     * @return The POM content with updated ci friendly properties
     */
    public String replace(String pom) {
        if (revision.isPresent()) {
            String element = revision.filter(Strings::isNotBlank)
                    .map(r -> format("<revision>%s</revision>", r))
                    .orElse("<revision/>");
            pom = pom.replaceAll("(?s)(<properties.*)<revision\\s*>.*</revision\\s*>|<revision\\s*/>(.*properties>)",
                    format("$1%s$2", element));
        }
        if (sha1.isPresent()) {
            String element = sha1.filter(Strings::isNotBlank)
                    .map(s -> format("<sha1>%s</sha1>", s))
                    .orElse("<sha1/>");
            pom = pom.replaceAll("(?s)(<properties.*)<sha1\\s*>.*</sha1\\s*>|<sha1\\s*/>(.*properties>)",
                    format("$1%s$2", element));
        }
        if (changelist.isPresent()) {
            String element = changelist.filter(Strings::isNotBlank)
                    .map(c -> format("<changelist>%s</changelist>", c))
                    .orElse("<changelist/>");
            pom = pom.replaceAll("(?s)(<properties.*)<changelist\\s*>.*</changelist\\s*>|<changelist\\s*/>(.*properties>)",
                    format("$1%s$2", element));
        }
        return pom;
    }

    public CiVersion withMissingFrom(Properties properties)
            throws MojoFailureException {
        if (!revision.isPresent() && properties.containsKey("revision")) {
            String revision = trim(properties.getProperty("revision"));
            if (parseBoolean(revision)) {
                throw new MojoFailureException("The revision property must be specified as a string value");
            }
            withRevision(revision);
        }
        if (!sha1.isPresent() && properties.containsKey("sha1")) {
            String sha1 = trim(properties.getProperty("sha1"));
            if (parseBoolean(sha1)) {
                throw new MojoFailureException("The sha1 property must be specified as a string value");
            }
            withSha1(sha1);
        }
        if (!changelist.isPresent() && properties.containsKey("changelist")) {
            String changelist = trim(properties.getProperty("changelist"));
            if (parseBoolean(changelist)) {
                throw new MojoFailureException("The changelist property must be specified as a string value");
            }
            withChangelist(changelist);
        }
        return this;
    }

    public CiVersion next(Incrementor incrementor)
            throws MojoFailureException {
        return new CiVersion()
                .withRevision(nextRevision(incrementor))
                .withSha1(sha1)
                .withChangelist(changelist);
    }

    public CiVersion release() {
        return changelist.filter("-SNAPSHOT"::equalsIgnoreCase)
                .map(value -> withChangelist(empty()))
                .orElseGet(() ->
                        revision.filter(value -> value.endsWith("-SNAPSHOT"))
                                .map(value -> value.substring(0, value.length() - 9))
                                .map(this::withRevision)
                                .orElse(CiVersion.this));
    }

    public String toExternalForm() {
        StringBuilder builder = new StringBuilder();
        revision.ifPresent(builder::append);
        sha1.ifPresent(builder::append);
        changelist.ifPresent(builder::append);
        return builder.toString();
    }

    public String toComponentForm() {
        StringBuilder builder = new StringBuilder();
        revision.ifPresent(value -> builder
                .append("revision:")
                .append(value)
                .append(" "));
        sha1.ifPresent(value -> builder
                .append("sha1:")
                .append(value)
                .append(" "));
        changelist.ifPresent(value -> builder
                .append("changelist:")
                .append(value)
                .append(" "));
        return builder.toString()
                .trim();
    }

    public void applyTo(Model model) {
        model.setVersion(toExternalForm());
        Properties properties = model.getProperties();
        revision.ifPresent(value -> properties.setProperty("revision", value));
        sha1.ifPresent(value -> properties.setProperty("sha1", value));
        changelist.ifPresent(value -> properties.setProperty("changelist", value));
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
        return toExternalForm();
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, sha1, changelist);
    }

    private String nextRevision(Incrementor incrementor)
            throws MojoFailureException {
        String revision = this.revision.orElseThrow(() ->
                new MojoFailureException("Failed to determine next version, revision ci property not detected."));
        try {
            return incrementor.next(parseVersion(revision))
                    .toString();
        } catch (Exception e) {
            throw new MojoFailureException(format("Revision %s is not a valid Maven artifact version", revision), e);
        }
    }

    public static CiVersion versionFrom(Properties properties)
            throws MojoFailureException {
        return new CiVersion().withMissingFrom(properties);
    }
}
