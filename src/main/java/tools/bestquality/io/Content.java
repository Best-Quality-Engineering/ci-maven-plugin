package tools.bestquality.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllBytes;

public class Content {

    public String read(Path path)
            throws IOException {
        return new String(readAllBytes(path), UTF_8);
    }

    public void write(Path path, String content)
            throws IOException {
        try (BufferedWriter writer = newBufferedWriter(path, UTF_8)) {
            writer.append(content);
        }
    }
}
