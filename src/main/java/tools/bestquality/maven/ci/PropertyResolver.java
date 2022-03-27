package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.Properties;
import java.util.function.Supplier;

public class PropertyResolver {
    private final Supplier<String> nullSupplier = () -> null;
    private final MavenProject project;
    private final MavenSession session;

    public PropertyResolver(MavenProject project, MavenSession session) {
        this.project = project;
        this.session = session;
    }

    public String resolve(String property, Supplier<String> defaultValue) {
        Properties systemProperties = session.getSystemProperties();
        if (systemProperties.containsKey(property)) {
            return systemProperties.getProperty(property);
        }
        Properties properties = project.getProperties();
        return properties.containsKey(property)
                ? properties.getProperty(property)
                : defaultValue.get();
    }

    public String resolve(String property) {
        return resolve(property, nullSupplier);
    }
}
