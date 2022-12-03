package tools.bestquality.maven.ci;

import org.assertj.core.api.AbstractAssert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.lang.String.format;

public class DocumentAssert
        extends AbstractAssert<DocumentAssert, String> {
    private final ReplaceContentAssert parent;
    private final Charset charset;

    public DocumentAssert(ReplaceContentAssert parent, String content, Charset charset) {
        super(content, DocumentAssert.class);
        this.parent = parent;
        this.charset = charset;
    }

    public DocumentAssert isEqualToTestResource(String resource, String... args)
            throws IOException {
        return isEqualTo(readTestResource(resource));
    }

    public DocumentAssert isEqualToFormattedTestResource(String resource, String... args)
            throws IOException {
        InputStream stream = getClass().getResourceAsStream(resource);
        return isEqualTo(format(readTestResource(resource), args));
    }

    public ReplaceContentAssert and() {
        return parent;
    }

    private String readTestResource(String resource)
            throws IOException {
        InputStream stream = getClass().getResourceAsStream(resource);
        return new String(stream.readAllBytes(), charset);
    }
}
