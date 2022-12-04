package tools.bestquality.maven.util;

import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;
import tools.bestquality.io.Document;

import static java.lang.Boolean.parseBoolean;

public class DocumentsConverter
        extends AbstractBasicConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return Document[].class.equals(type);
    }

    @Override
    protected Object fromString(String value) {
        if (parseBoolean(value)) {

        }
        return new Document[0];
    }
}
