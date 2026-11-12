
package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BoardView.
 * Author: LETI-123856
 * Date: 12/11/2026 12:30
 *
 * Tests the Singleton behavior, headless environment,
 * and graphical representation of the game board.
 *
 * Cyclomatic Complexity:
 * - getInstance(): 3
 * - updateFromMap(): depends on switch branch counting
 * - Constructor: contains nested loops
 */
@DisplayName("Tests for the BoardView class")
class BoardViewTest {

    private BoardView boardView;
    private char[][] map;

    @BeforeEach
    void setUp() {
        map = new char[10][10];

        for (char[] row : map) {
            Arrays.fill(row, '.');
        }

        if (!GraphicsEnvironment.isHeadless()) {
            boardView = BoardView.getInstance();
        }
    }

    @AfterEach
    void tearDown() {
        if (boardView != null) {
            boardView.setVisible(false);
        }

        boardView = null;
        map = null;
    }

    @Test
    @DisplayName("getInstance: returns null in headless mode or a valid instance in graphical mode")
    void getInstance1() {
        BoardView instance = BoardView.getInstance();

        if (GraphicsEnvironment.isHeadless()) {
            assertNull(instance,
                    "Error: Expected null in headless mode.");
        } else {
            assertNotNull(instance,
                    "Error: Expected a BoardView instance.");
        }
    }

    @Test
    @DisplayName("getInstance: returns the same instance when called twice")
    void getInstance2() {
        BoardView first = BoardView.getInstance();
        BoardView second = BoardView.getInstance();

        if (GraphicsEnvironment.isHeadless()) {
            assertAll(
                    () -> assertNull(first,
                            "Error: Expected null in headless mode."),
                    () -> assertNull(second,
                            "Error: Expected null in headless mode.")
            );
        } else {
            assertSame(first, second,
                    "Error: Expected the same Singleton instance.");
        }
    }

    @Test
    @DisplayName("updateFromMap: displays a hit as a red cell with X")
    void updateFromMap1() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: No graphical window should be created.");
            return;
        }

        map[0][0] = '*';
        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("X", cell.getText(),
                        "Error: Expected X for a hit."),
                () -> assertEquals(new Color(211, 47, 47),
                        cell.getBackground(),
                        "Error: Expected red for a hit.")
        );
    }

    @Test
    @DisplayName("updateFromMap: displays a miss as a blue cell")
    void updateFromMap2() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: Expected no window in headless mode.");
            return;
        }

        map[0][0] = 'o';
        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("~", cell.getText(),
                        "Error: Expected ~ for a missed shot."),
                () -> assertEquals(new Color(30, 136, 229),
                        cell.getBackground(),
                        "Error: Expected blue for a missed shot.")
        );
    }

    @Test
    @DisplayName("updateFromMap: displays a ship as a green cell")
    void updateFromMap3() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: Expected no window in headless mode.");
            return;
        }

        map[0][0] = '#';
        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("#", cell.getText(),
                        "Error: Expected # for a ship."),
                () -> assertEquals(new Color(76, 175, 80),
                        cell.getBackground(),
                        "Error: Expected green for a ship.")
        );
    }

    @Test
    @DisplayName("updateFromMap: displays adjacent cells in dark gray")
    void updateFromMap4() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: Expected no window in headless mode.");
            return;
        }

        map[0][0] = '-';
        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("", cell.getText(),
                        "Error: Expected empty text for adjacent cells."),
                () -> assertEquals(new Color(55, 71, 79),
                        cell.getBackground(),
                        "Error: Expected dark gray for adjacent cells.")
        );
    }

    @Test
    @DisplayName("updateFromMap: displays unexplored water in dark blue")
    void updateFromMap5() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: Expected no window in headless mode.");
            return;
        }

        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("", cell.getText(),
                        "Error: Expected empty text for unexplored water."),
                () -> assertEquals(new Color(35, 45, 60),
                        cell.getBackground(),
                        "Error: Expected dark blue for unexplored water.")
        );
    }

    @Test
    @DisplayName("updateFromMap: uses default formatting for unknown symbols")
    void updateFromMap6() {
        if (GraphicsEnvironment.isHeadless()) {
            assertNull(BoardView.getInstance(),
                    "Error: Expected no window in headless mode.");
            return;
        }

        map[0][0] = '?';
        boardView.updateFromMap(map);

        JButton cell = getCell(0, 0);

        assertAll(
                () -> assertEquals("", cell.getText(),
                        "Error: Expected empty text for unknown symbols."),
                () -> assertEquals(new Color(35, 45, 60),
                        cell.getBackground(),
                        "Error: Expected default water color.")
        );
    }

    private JButton getCell(int row, int column) {
        JPanel panel = (JPanel) boardView.getContentPane()
                .getComponent(0);

        int index = 11 + row * 11 + 1 + column;
        Component component = panel.getComponent(index);

        assertInstanceOf(JButton.class, component,
                "Error: Expected a JButton at the selected position.");

        return (JButton) component;
    }
}
