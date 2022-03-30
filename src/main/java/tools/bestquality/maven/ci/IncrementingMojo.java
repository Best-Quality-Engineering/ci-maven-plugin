package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.lang.String.format;
import static tools.bestquality.maven.ci.CiVersion.versionFrom;
import static tools.bestquality.maven.versioning.StandardIncrementor.incrementor;

public abstract class IncrementingMojo<M extends IncrementingMojo<M>>
        extends CiMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(alias = "incrementor", property = "incrementor", defaultValue = "auto")
    private String incrementor;


    @SuppressWarnings("unchecked")
    public M withProject(MavenProject project) {
        this.project = project;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withIncrementor(String incrementor) {
        this.incrementor = incrementor;
        return (M) this;
    }

    protected CiVersion current() {
        return versionFrom(project.getProperties());
    }

    protected CiVersion next()
            throws MojoFailureException {
        CiVersion current = current();
        CiVersion next = current.next(incrementor(incrementor));
        info(format("Next revision is: %s", next.toExternalForm()));
        return next;
    }
}
