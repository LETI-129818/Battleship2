
package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameDatabase.
 *
 *  * Author: LETI-123856
 *  * Date: 12/11/2026 12:30
 *
 * Tests JSON saving, loading and error handling.
 */
@DisplayName("Tests for the GameDatabase class")
class GameDatabaseTest {

    private GameDatabase database;
    private Path filePath;
    private byte[] originalContent;
    private boolean fileExisted;

    @BeforeEach
    void setUp() throws IOException {
        database = new GameDatabase();
        filePath = Path.of("jogadas.json");

        // Preserve any existing game history.
        fileExisted = Files.exists(filePath);

        if (fileExisted) {
            originalContent = Files.readAllBytes(filePath);
        }

        Files.deleteIfExists(filePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(filePath);

        // Restore the original game history.
        if (fileExisted) {
            Files.write(filePath, originalContent);
        }

        database = null;
    }

    @Test
    @DisplayName("saveShots: creates a JSON file")
    void saveShots1() {
        List<ShotRecord> shots = new ArrayList<>();

        database.saveShots(shots);

        assertTrue(Files.exists(filePath),
                "Error: JSON file should have been created.");
    }

    @Test
    @DisplayName("saveShots: saves an empty list as a JSON array")
    void saveShots2() throws IOException {
        List<ShotRecord> shots = new ArrayList<>();

        database.saveShots(shots);

        String content = Files.readString(filePath);

        assertEquals("[]", content.replaceAll("\\s+", ""),
                "Error: Empty list should be saved as [].");
    }

    @Test
    @DisplayName("loadShots: returns an empty list when file does not exist")
    void loadShots1() {
        List<ShotRecord> result = database.loadShots();

        assertAll(
                () -> assertNotNull(result,
                        "Error: Result should not be null."),
                () -> assertTrue(result.isEmpty(),
                        "Error: Expected an empty list.")
        );
    }

    @Test
    @DisplayName("loadShots: loads a valid empty JSON array")
    void loadShots2() throws IOException {
        Files.writeString(filePath, "[]");

        List<ShotRecord> result = database.loadShots();

        assertAll(
                () -> assertNotNull(result,
                        "Error: Result should not be null."),
                () -> assertEquals(0, result.size(),
                        "Error: Expected zero saved shots.")
        );
    }

    @Test
    @DisplayName("loadShots: handles invalid JSON")
    void loadShots3() throws IOException {
        Files.writeString(filePath, "{ invalid json }");

        List<ShotRecord> result = database.loadShots();

        assertAll(
                () -> assertNotNull(result,
                        "Error: Result should not be null."),
                () -> assertTrue(result.isEmpty(),
                        "Error: Invalid JSON should return an empty list.")
        );
    }

    @Test
    @DisplayName("loadShots: handles an empty file")
    void loadShots4() throws IOException {
        Files.writeString(filePath, "");

        List<ShotRecord> result = database.loadShots();

        assertNotNull(result,
                "Error: Loading should return a list.");
        assertTrue(result.isEmpty(),
                "Error: Empty file should return an empty list.");
    }

    @Test
    @DisplayName("saveShots and loadShots: round-trip an empty list")
    void saveAndLoadShots() {
        database.saveShots(new ArrayList<>());

        List<ShotRecord> result = database.loadShots();

        assertAll(
                () -> assertTrue(Files.exists(filePath),
                        "Error: JSON file should exist."),
                () -> assertNotNull(result,
                        "Error: Loaded list should not be null."),
                () -> assertEquals(0, result.size(),
                        "Error: Loaded list should be empty.")
        );
    }
}
