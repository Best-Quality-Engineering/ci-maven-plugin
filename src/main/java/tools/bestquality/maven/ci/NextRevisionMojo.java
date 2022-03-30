package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "next-revision",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class NextRevisionMojo
        extends IncrementingMojo<NextRevisionMojo> {

    @Override
    public void execute()
            throws MojoFailureException, MojoExecutionException {
        outputNextRevision();
    }
}
