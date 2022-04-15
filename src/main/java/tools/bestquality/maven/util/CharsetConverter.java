package tools.bestquality.maven.util;

import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;

import java.nio.charset.Charset;

public class CharsetConverter
        extends AbstractBasicConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return Charset.class.equals(type);
    }

    @Override
    protected Object fromString(String value) {
        return Charset.forName(value);
    }
}
