package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe ScoreEntry")
class ScoreEntryTest {

    @Test
    @DisplayName("Os acessores devolvem os valores passados ao construtor")
    void accessorsReturnConstructorValues() {
        ScoreEntry e = new ScoreEntry("Ana", "2026-11-15T20:00:00", 5, 15, 120L);

        assertAll(
                () -> assertEquals("Ana", e.player()),
                () -> assertEquals("2026-11-15T20:00:00", e.date()),
                () -> assertEquals(5, e.bursts()),
                () -> assertEquals(15, e.shots()),
                () -> assertEquals(120L, e.durationSeconds())
        );
    }

    @Test
    @DisplayName("Duas entradas com os mesmos valores são iguais")
    void equalEntries() {
        ScoreEntry a = new ScoreEntry("Ana", "d", 5, 15, 120L);
        ScoreEntry b = new ScoreEntry("Ana", "d", 5, 15, 120L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Entradas com valores diferentes não são iguais")
    void differentEntries() {
        ScoreEntry a = new ScoreEntry("Ana", "d", 5, 15, 120L);
        ScoreEntry b = new ScoreEntry("Rui", "d", 5, 15, 120L);

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("toString inclui o nome do jogador")
    void toStringContainsPlayer() {
        ScoreEntry e = new ScoreEntry("Ana", "d", 5, 15, 120L);

        assertTrue(e.toString().contains("Ana"));
    }
}