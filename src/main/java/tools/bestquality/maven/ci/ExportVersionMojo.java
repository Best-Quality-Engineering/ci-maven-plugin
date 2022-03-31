package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.bestquality.io.Content;

import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.file.Files.createDirectories;
import static tools.bestquality.maven.ci.CiVersion.versionFrom;

public abstract class ExportVersionMojo<M extends ExportVersionMojo<M>>
        extends CiMojo {
    protected final Content content;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/ci")
    protected File outputDirectory;

    @Parameter(alias = "scriptable", property = "scriptable", defaultValue = "false")
    protected boolean scriptable;

    public ExportVersionMojo(Content content) {
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    public M withProject(MavenProject project) {
        this.project = project;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M withScriptable(boolean forceStdout) {
        this.scriptable = forceStdout;
        return (M) this;
    }

    protected CiVersion current() {
        return versionFrom(project.getProperties());
    }

    protected void exportVersion(String filename, String version)
            throws MojoExecutionException {
        if (scriptable) {
            out.print(version);
            out.flush();
        } else {
            Path directory = outputDirectory.toPath();
            Path file = directory.resolve(filename);
            info(format("Exporting version to %s", file.toAbsolutePath()));
            try {
                createDirectories(directory);
                content.write(file, version);
            } catch (Exception e) {
                error(format("Failure exporting version to: %s", file.toAbsolutePath()), e);
                throw new MojoExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }
}
