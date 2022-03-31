package tools.bestquality.maven.test

import groovy.xml.XmlParser
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import spock.lang.Specification

import java.nio.file.Path

import static java.nio.charset.StandardCharsets.UTF_8
import static java.nio.file.Files.copy
import static java.nio.file.Files.createTempDirectory
import static java.nio.file.Files.createTempFile
import static java.nio.file.Files.delete
import static java.nio.file.Files.newBufferedWriter
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

class MojoSpecification
        extends Specification {
    protected XmlParser parser

    protected Log logMock

    protected MavenProject projectMock
    protected Path pom
    protected Properties projectProperties

    protected MavenSession sessionMock
    protected Properties systemProperties

    protected Path outputPath

    def setup() {
        parser = new XmlParser()

        logMock = Mock(Log)

        projectMock = Mock(MavenProject)
        pom = createTempFile("pom-", ".xml")
        projectMock.getFile() >> pom.toFile()
        projectProperties = new Properties()
        projectMock.getProperties() >> projectProperties

        sessionMock = Mock(MavenSession)
        systemProperties = new Properties()
        sessionMock.getSystemProperties() >> systemProperties

        outputPath = createTempDirectory("mojo-test-")
    }

    def setupPom(String contents) {
        try (BufferedWriter writer = newBufferedWriter(pom, UTF_8)) {
            writer.append(contents)
        }
    }

    def setupPom(InputStream contents) {
        copy(contents, pom, REPLACE_EXISTING)
    }

    def setupPomFromResource(String resource) {
        setupPom(getClass().getResourceAsStream(resource))
    }

    def cleanup() {
        outputPath.deleteDir()
        delete(pom)
    }
}
