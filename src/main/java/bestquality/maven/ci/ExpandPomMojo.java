package bestquality.maven.ci;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static bestquality.maven.ci.VersionTemplate.template;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllBytes;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;


@Mojo(name = "expand-pom", requiresDependencyCollection = RUNTIME, threadSafe = true, defaultPhase = PROCESS_RESOURCES)
public class ExpandPomMojo
        extends CiMojo<ExpandPomMojo> {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(property = "sha1")
    private String sha1;

    @Parameter(property = "changelist")
    private String changelist;

    public ExpandPomMojo withProject(MavenProject project) {
        this.project = project;
        return this;
    }

    public ExpandPomMojo withSession(MavenSession session) {
        this.session = session;
        return this;
    }

    public ExpandPomMojo withSha1(String sha1) {
        this.sha1 = sha1;
        return this;
    }

    public ExpandPomMojo withChangelist(String changelist) {
        this.changelist = changelist;
        return this;
    }

    public void execute()
            throws MojoExecutionException {
        String templatePom = readCurrentPom();
        String expandedPom = template(templatePom)
                .expand(determineRevision(), sha1, changelist);
        if (!templatePom.equals(expandedPom)) {
            Path ciPomFile = writePom(expandedPom);
            project.setPomFile(ciPomFile.toFile());
            getLog().info(format("Configured %s to use generated POM file at %s",
                    project.getId(), ciPomFile.toAbsolutePath()));
        }
    }

    private Path writePom(String content)
            throws MojoExecutionException {
        Path ciPomPath = ciPomPath();
        try {
            createDirectories(ciPomPath);
            try (BufferedWriter writer = newBufferedWriter(ciPomPath, UTF_8)) {
                writer.append(content);
            }
            return ciPomPath;
        } catch (IOException e) {
            getLog().error(format("Failure writing generated POM file", ciPomPath), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private String readCurrentPom()
            throws MojoExecutionException {
        File currentPomFile = project.getFile();
        try {
            return new String(readAllBytes(currentPomFile.toPath()), UTF_8);
        } catch (IOException e) {
            getLog().error(format("Failure reading project POM file", currentPomFile), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private String determineRevision() {
        Properties properties = project.getProperties();
        if (properties.containsKey("internal.revision")) {
            return properties.getProperty("internal.revision");
        }
        Properties systemProperties = session.getSystemProperties();
        if (systemProperties.containsKey("revision")) {
            return systemProperties.getProperty("revision");
        } else if (properties.containsKey("revision")) {
            return properties.getProperty("revision");
        } else {
            return project.getVersion();
        }
    }
}
