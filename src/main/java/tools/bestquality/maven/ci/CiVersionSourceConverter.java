package tools.bestquality.maven.ci;

import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;

import static tools.bestquality.maven.ci.CiVersionSources.source;

public class CiVersionSourceConverter
        extends AbstractBasicConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return CiVersionSource.class.equals(type);
    }

    @Override
    protected Object fromString(String value) {
        return source(value);
    }
}
