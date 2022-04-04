package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

@FunctionalInterface
public interface CiVersionSource {
    CiVersion from(MavenProject project, MavenSession session)
            throws MojoFailureException;
}
