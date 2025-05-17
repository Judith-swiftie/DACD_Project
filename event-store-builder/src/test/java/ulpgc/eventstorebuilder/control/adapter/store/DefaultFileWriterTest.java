package ulpgc.eventstorebuilder.control.adapter.store;

import ulpgc.eventstorebuilder.control.port.FileWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DefaultFileWriterTest {
    private FileWriter fileWriter;
    @BeforeEach
    void setUp() {
        fileWriter = new DefaultFileWriter();
    }

    @Test
    void testAppend_CreatesFileAndAppendsContent(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("testDir/testFile.txt");
        String content = "line 1";
        fileWriter.append(filePath, content);
        fileWriter.append(filePath, "line 2");
        List<String> lines = Files.readAllLines(filePath);
        assertEquals(2, lines.size());
        assertEquals("line 1", lines.get(0));
        assertEquals("line 2", lines.get(1));
    }

    @Test
    void testAppend_CreatesMissingDirectories(@TempDir Path tempDir) throws IOException {
        Path nestedPath = tempDir.resolve("a/b/c/d/e.txt");
        String content = "deep content";
        assertFalse(Files.exists(nestedPath.getParent()));
        fileWriter.append(nestedPath, content);
        assertTrue(Files.exists(nestedPath));
        List<String> lines = Files.readAllLines(nestedPath);
        assertEquals(List.of("deep content"), lines);
    }
}
