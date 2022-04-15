package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import tools.bestquality.io.Content;
import tools.bestquality.maven.versioning.Incrementor;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "increment-pom",
        configurator = "ci-mojo-configurator",
        aggregator = true,
        threadSafe = true,
        defaultPhase = VALIDATE)
public class IncrementPomMojo
        extends ExportVersionMojo<IncrementPomMojo> {

    /**
     * The strategy used to increment the version number. Possible values are:
     * <p/>
     * <ul>
     *     <li><code>build</code></li>
     *     <li><code>patch</code></li>
     *     <li><code>minor</code></li>
     *     <li><code>major</code></li>
     *     <li><code>auto</code></li>
     * </ul>
     * <p/>
     * The default value <code>auto</code> will search for the first non-null component
     * and increment that value, starting with <code>build</code> and working toward
     * <code>major</code>
     */
    @Parameter(alias = "incrementor", property = "incrementor", defaultValue = "auto")
    private Incrementor incrementor;

    /**
     * The filename in the output directory which will contain the exported release version.
     * <p/>
     * This file will not be generated when the <code>scriptable</code> property is
     * <code>true</code>
     */
    @Parameter(property = "filename", defaultValue = "next-version.txt")
    private String filename;

    @Inject
    public IncrementPomMojo(Content content) {
        super(content);
    }

    public IncrementPomMojo withIncrementor(Incrementor incrementor) {
        this.incrementor = incrementor;
        return this;
    }

    public IncrementPomMojo withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        CiVersion next = next();
        writeIncrementedPom(next.replace(readProjectPom()));
        exportVersion(filename, next.toExternalForm());
    }

    CiVersion next()
            throws MojoFailureException {
        CiVersion current = current();
        CiVersion next = current.next(incrementor);
        info(format("Next ci version is: %s", next.toExternalForm()));
        return next;
    }

    private String readProjectPom()
            throws MojoExecutionException {
        info("Reading project POM file");
        File pomFile = project.getFile();
        try {
            return content.read(pomFile.toPath(), charset(project));
        } catch (Exception e) {
            error(format("Failure reading project POM file: %s", pomFile.getAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private void writeIncrementedPom(String pom)
            throws MojoExecutionException {
        Path pomPath = project.getFile().toPath();
        info(format("Writing incremented POM file to %s", pomPath.toAbsolutePath()));
        try {
            content.write(pomPath, charset(project), pom);
        } catch (Exception e) {
            error(format("Failure writing incremented POM file: %s", pomPath.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
