package tools.bestquality.maven.ci;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.nio.charset.Charset;

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

    public static Charset charset(MavenProject project) {
        String encoding = project.getModel()
                .getModelEncoding();
        return Charset.forName(encoding);
    }
}
