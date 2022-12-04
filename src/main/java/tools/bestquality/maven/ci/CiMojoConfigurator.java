package tools.bestquality.maven.ci;

import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import tools.bestquality.maven.util.CharsetConverter;
import tools.bestquality.maven.util.DocumentsConverter;
import tools.bestquality.maven.util.PathConverter;
import tools.bestquality.maven.util.PatternConverter;
import tools.bestquality.maven.versioning.IncrementorConverter;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named("ci-mojo-configurator")
public class CiMojoConfigurator
        extends BasicComponentConfigurator {

    @PostConstruct
    public void initialize() {
        converterLookup.registerConverter(new IncrementorConverter());
        converterLookup.registerConverter(new CiVersionSourceConverter());
        converterLookup.registerConverter(new CharsetConverter());
        converterLookup.registerConverter(new DocumentsConverter());
        converterLookup.registerConverter(new PathConverter());
        converterLookup.registerConverter(new PatternConverter());
    }
}
