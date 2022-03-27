package tools.bestquality.maven.ci;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllBytes;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;
import static tools.bestquality.maven.ci.VersionTemplate.template;


@Mojo(name = "expand-pom", requiresDependencyCollection = RUNTIME, threadSafe = true, defaultPhase = PROCESS_RESOURCES)
public class ExpandPomMojo
        extends CiMojo<ExpandPomMojo> {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component
    private PropertyResolver resolver;

    public ExpandPomMojo withProject(MavenProject project) {
        this.project = project;
        return this;
    }

    public ExpandPomMojo withPropertyResolver(PropertyResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    public void execute()
            throws MojoExecutionException {
        String templatePom = readTemplatePom();
        String expandedPom = template(templatePom)
                .expand(resolver.resolve("revision"),
                        resolver.resolve("sha1"),
                        resolver.resolve("changelist"));
        if (!templatePom.equals(expandedPom)) {
            Path ciPomFile = writeCiPom(expandedPom);
            project.setPomFile(ciPomFile.toFile());
            getLog().info(format("Configured %s to use generated POM file at %s",
                    project.getId(), ciPomFile.toAbsolutePath()));
        }
    }

    private String readTemplatePom()
            throws MojoExecutionException {
        File currentPomFile = project.getFile();
        try {
            return new String(readAllBytes(currentPomFile.toPath()), UTF_8);
        } catch (IOException e) {
            getLog().error(format("Failure reading project POM file: %s", currentPomFile.getAbsolutePath()), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private Path writeCiPom(String content)
            throws MojoExecutionException {
        Path ciPomPath = ciPomPath();
        try {
            createDirectories(ciPomPath.getParent());
            try (BufferedWriter writer = newBufferedWriter(ciPomPath, UTF_8)) {
                writer.append(content);
            }
            return ciPomPath;
        } catch (IOException e) {
            getLog().error(format("Failure writing generated POM file: %s", ciPomPath.toAbsolutePath()), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
