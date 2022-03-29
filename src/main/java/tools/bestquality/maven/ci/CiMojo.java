package tools.bestquality.maven.ci;

import org.apache.maven.plugin.AbstractMojo;

public abstract class CiMojo
        extends AbstractMojo {

    protected void info(CharSequence message) {
        getLog().info(message);
    }

    protected void error(CharSequence message, Throwable error) {
        getLog().error(message, error);
    }

    protected void warn(CharSequence message) {
        getLog().warn(message);
    }
}
