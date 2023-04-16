package tools.bestquality.maven.ci;

import org.assertj.core.api.AbstractAssert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

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

    public DocumentAssert isEqualToFormattedTestResource(String resource, Object... args)
            throws IOException {
        return isEqualTo(format(readTestResource(resource), args));
    }

    public ReplaceContentAssert and() {
        return parent;
    }

    private String readTestResource(String resource)
            throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(resource)) {
            assertThat(stream)
                    .isNotNull();
            return new String(stream.readAllBytes(), charset);
        }
    }
}
