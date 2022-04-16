package tools.bestquality.io;

import tools.bestquality.maven.ci.CiVersion;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    private Charset encoding;
    private Path location;
    private Pattern pattern;
    private String replacement;

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Document withEncoding(Charset encoding) {
        setEncoding(encoding);
        return this;
    }

    public Path getLocation() {
        return location;
    }

    public void setLocation(Path location) {
        this.location = location;
    }

    public Document withLocation(Path location) {
        setLocation(location);
        return this;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Document withPattern(Pattern pattern) {
        setPattern(pattern);
        return this;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public Document withReplacement(String replacement) {
        setReplacement(replacement);
        return this;
    }

    public void updateTo(Content content, CiVersion version)
            throws IOException {
        Matcher matcher = pattern.matcher(content.read(location, encoding));
        content.write(location, encoding, matcher.replaceAll(replacement(version)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(encoding, document.encoding)
                && Objects.equals(location, document.location)
                && Objects.equals(pattern(), document.pattern())
                && Objects.equals(replacement, document.replacement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encoding, location, pattern(), replacement);
    }

    private String pattern() {
        return pattern != null
                ? pattern.pattern()
                : null;
    }

    private String replacement(CiVersion version) {
        return replacement.replace("${version}", version.toExternalForm());
    }
}
