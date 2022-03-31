package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import tools.bestquality.io.Content;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "next-revision",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class NextRevisionMojo
        extends IncrementingMojo<NextRevisionMojo> {

    NextRevisionMojo(Content content) {
        super(content);
    }

    public NextRevisionMojo() {
        this(new Content());
    }

    @Override
    public void execute()
            throws MojoFailureException, MojoExecutionException {
        outputNextRevision(next());
    }
}
