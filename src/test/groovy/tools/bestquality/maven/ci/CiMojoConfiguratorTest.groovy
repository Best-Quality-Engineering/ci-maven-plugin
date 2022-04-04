package tools.bestquality.maven.ci

import spock.lang.Specification
import tools.bestquality.maven.versioning.Incrementor
import tools.bestquality.maven.versioning.IncrementorConverter

class CiMojoConfiguratorTest
        extends Specification {
    CiMojoConfigurator configurator

    def setup() {
        configurator = new CiMojoConfigurator()
    }

    def "should register converters"() {
        when:
        configurator.initialize()

        then:
        configurator.converterLookup.lookupConverterForType(Incrementor.class) instanceof IncrementorConverter;
        configurator.converterLookup.lookupConverterForType(CiVersionSource.class) instanceof CiVersionSourceConverter;
    }
}
