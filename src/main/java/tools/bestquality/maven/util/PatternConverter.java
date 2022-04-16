package tools.bestquality.maven.util;

import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;

import java.util.regex.Pattern;

public class PatternConverter
        extends AbstractBasicConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return Pattern.class.equals(type);
    }

    @Override
    protected Object fromString(String value) {
        return Pattern.compile(value);
    }
}
