
package battleship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class IMoveTest {

    @Test
    @DisplayName("Ler uma jogada sem tiros")
    void readMove() {

        Scanner sc = new Scanner("0");

        Move move = IMove.readMove(1, sc);

        assertEquals(1, move.getNumber());
        assertTrue(move.getShots().isEmpty());

        sc.close();
    }

    @Test
    @DisplayName("Verificar toString")
    void testToString() {

        Move move = IMove.readMove(1, new Scanner("0"));

        assertEquals(
                "Move{number=1, shots=0, results=0}",
                move.toString()
        );
    }

    @Test
    @DisplayName("Verificar número da jogada")
    void getNumber() {

        Move move = IMove.readMove(5, new Scanner("0"));

        assertEquals(5, move.getNumber());
    }

    @Test
    @DisplayName("Verificar lista de tiros")
    void getShots() {

        Scanner sc = new Scanner("2 1 2 3 4");

        Move move = IMove.readMove(1, sc);

        assertEquals(2, move.getShots().size());

        sc.close();
    }

    @Test
    @DisplayName("Verificar lista de resultados dos tiros")
    void getShotResults() {

        Move move = IMove.readMove(1, new Scanner("0"));

        assertNotNull(move.getShotResults());
        assertTrue(move.getShotResults().isEmpty());
    }

    @Test
    @DisplayName("Verificar processamento dos tiros")
    void processEnemyFire() {

        Move move = IMove.readMove(1, new Scanner("0"));

        assertNotNull(move.processEnemyFire(false));
    }
}
