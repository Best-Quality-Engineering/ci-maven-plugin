package tools.bestquality.maven.util;

import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter
        extends AbstractBasicConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return Path.class.equals(type);
    }

    @Override
    protected Object fromString(String value) {
        return Paths.get(value);
    }
}
