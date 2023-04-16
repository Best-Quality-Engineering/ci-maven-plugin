package tools.bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.io.Content;
import tools.bestquality.io.ModelReader;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

@Mojo(name = "expand-pom",
        configurator = "ci-mojo-configurator",
        threadSafe = true,
        defaultPhase = VALIDATE)
public class ExpandPomMojo
        extends CiPomMojo<ExpandPomMojo> {
    private final Content content;
    private final ModelReader reader;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * The source of the properties used to compute the CI Version. There are 2
     * primary sources of properties from which the CI version is built:
     * <p/>
     * <ul>
     *     <li>The maven project's properties</li>
     *     <li>The maven session's system properties</li>
     * </ul>
     * <p/>
     * This parameter determines which set of properties is used, there are options
     * for merging the properties as well. The possible values are:
     * <p/>
     * <ul>
     *     <li><code>project</code></li>
     *     <li><code>system</code></li>
     *     <li><code>merge-system-first</code></li>
     *     <li><code>merge-project-first</code></li>
     * </ul>
     */
    @Parameter(alias = "source", property = "source", defaultValue = "merge-system-first")
    private CiVersionSource source;

    @Inject
    public ExpandPomMojo(Content content, ModelReader reader) {
        this.content = content;
        this.reader = reader;
    }

    public ExpandPomMojo withProject(MavenProject project) {
        this.project = project;
        return this;
    }

    public ExpandPomMojo withSession(MavenSession session) {
        this.session = session;
        return this;
    }

    public ExpandPomMojo withSource(CiVersionSource source) {
        this.source = source;
        return this;
    }

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        String projectPom = readProjectPom();
        String expandedPom = expandProjectPom(projectPom);
        if (projectPom.equals(expandedPom)) {
            info("No changes detected in expanded POM, retaining current project POM file");
            return;
        }
        Path ciPomFile = writeCiPom(expandedPom);
        project.setPomFile(ciPomFile.toFile());
        // https://github.com/mojohaus/flatten-maven-plugin/issues/100#issuecomment-1088904752
        // https://github.com/apache/maven-shade-plugin/pull/129
        project.setOriginalModel(toModel(expandedPom));
        info(format("%s has been configured to use generated POM file at %s",
                project.getArtifactId(), ciPomFile.toAbsolutePath()));
    }

    private String readProjectPom()
            throws MojoExecutionException {
        info("Reading project POM file");
        File currentPomFile = project.getFile();
        try {
            return content.read(currentPomFile.toPath(), charset(project));
        } catch (Exception e) {
            error(format("Failure reading project POM file: %s", currentPomFile.getAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private CiVersion current()
            throws MojoFailureException {
        return source.from(project, session);
    }

    private String expandProjectPom(String projectPom)
            throws MojoExecutionException, MojoFailureException {
        info("Expanding contents of project POM file");
        CiVersion version = current();
        try {
            info(format("Expanding POM file with %s [%s]",
                    version.toExternalForm(), version.toComponentForm()));
            return version.replace(version.expand(projectPom));
        } catch (Exception e) {
            error("Failure expanding template POM file", e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private Path writeCiPom(String pom)
            throws MojoExecutionException {
        Path ciPomPath = ciPomPath();
        info(format("Writing expanded POM file to %s", ciPomPath.toAbsolutePath()));
        try {
            createDirectories(ciPomPath.getParent());
            content.write(ciPomPath, charset(project), pom);
            return ciPomPath;
        } catch (Exception e) {
            error(format("Failure writing expanded POM file: %s", ciPomPath.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private Model toModel(String content)
            throws MojoExecutionException {
        try {
            return reader.read(content);
        } catch (Exception e) {
            error("Failure reading model from expanded POM", e);
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }
}
