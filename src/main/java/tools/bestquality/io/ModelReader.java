package tools.bestquality.io;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringReader;

@Singleton
public class ModelReader {
    private final MavenXpp3Reader reader;

    public ModelReader() {
        this(new MavenXpp3Reader());
    }

    ModelReader(MavenXpp3Reader reader) {
        this.reader = reader;
    }

    public Model read(String content)
            throws XmlPullParserException, IOException {
        try (StringReader sr = new StringReader(content)) {
            return reader.read(sr);
        }
    }
}
