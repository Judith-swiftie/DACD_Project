package ulpgc.playlistsforevents.control.adapter.consumer;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class FileReaderService {
    public List<Path> listFiles(String directory, String extension) throws IOException {
        try (var stream = Files.walk(Paths.get(directory))) {
            return stream.filter(path -> path.toString().endsWith(extension))
                    .collect(Collectors.toList());
        }
    }

    public List<String> readLines(Path file) throws IOException {
        return Files.readAllLines(file);
    }
}
