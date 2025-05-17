package ulpgc.playlistsforevents.control.adapter.consumer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

public class FileReaderServiceTest {
    private FileReaderService fileReaderService;

    @BeforeEach
    public void setUp() {
        fileReaderService = new FileReaderService();
    }

    @Test
    public void testListFiles_returnsOnlyFilesWithExtension() throws IOException {
        Path tempDir = Files.createTempDirectory("testDir");
        try {
            Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
            Path file2 = Files.createFile(tempDir.resolve("file2.txt"));
            Path file3 = Files.createFile(tempDir.resolve("file3.json"));
            List<Path> txtFiles = fileReaderService.listFiles(tempDir.toString(), ".txt");
            assertTrue(txtFiles.contains(file1));
            assertTrue(txtFiles.contains(file2));
            assertFalse(txtFiles.contains(file3));
        } finally {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> p.toFile().delete());
        }
    }

    @Test
    public void testReadLines_readsAllLinesFromFile() throws IOException {
        Path tempFile = Files.createTempFile("testFile", ".txt");
        try {
            List<String> lines = List.of("line1", "line2", "line3");
            Files.write(tempFile, lines);
            List<String> readLines = fileReaderService.readLines(tempFile);
            assertEquals(lines, readLines);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
