package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de contrato da interface IPosition (implementação Position)")
class IPositionTest {

    private IPosition pos;

    @BeforeEach
    void setUp() {
        pos = new Position(2, 3);
    }

    @Nested
    @DisplayName("Coordenadas")
    class Coordinates {

        @Test
        @DisplayName("getRow e getColumn devolvem as coordenadas internas")
        void rowAndColumn() {
            assertEquals(2, pos.getRow());
            assertEquals(3, pos.getColumn());
        }

        @Test
        @DisplayName("getClassicRow e getClassicColumn devolvem a notação clássica (C4)")
        void classicCoordinates() {
            assertEquals('C', pos.getClassicRow());
            assertEquals(4, pos.getClassicColumn());
        }

        @Test
        @DisplayName("Construtor clássico ('A', 1) corresponde a (0, 0)")
        void classicConstructor() {
            IPosition p = new Position('A', 1);
            assertEquals(0, p.getRow());
            assertEquals(0, p.getColumn());
        }
    }

    @Nested
    @DisplayName("Igualdade")
    @SuppressWarnings({"EqualsWithItself", "EqualsBetweenInconvertibleTypes",
            "ConstantValue", "SimplifiableAssertion", "SimplifiableJUnitAssertion"})
    class Equality {

        @Test
        @DisplayName("Uma posição é igual a si própria")
        void equalsItself() {
            assertTrue(pos.equals(pos));
        }

        @Test
        @DisplayName("Posições com a mesma linha e coluna são iguais")
        void equalsSameCoordinates() {
            assertEquals(new Position(2, 3), pos);
        }

        @Test
        @DisplayName("Posições com linha ou coluna diferente não são iguais")
        void notEqualsDifferentCoordinates() {
            assertNotEquals(new Position(2, 4), pos);
            assertNotEquals(new Position(3, 3), pos);
        }

        @Test
        @DisplayName("Uma posição não é igual a um objeto de outro tipo nem a null")
        void notEqualsOtherType() {
            assertFalse(pos.equals("C4"));
            assertFalse(pos.equals(null));
        }
    }

    @Nested
    @DisplayName("Limites do tabuleiro")
    class Inside {

        @Test
        @DisplayName("Posições nos cantos estão dentro do tabuleiro")
        void cornersInside() {
            assertTrue(new Position(0, 0).isInside());
            assertTrue(new Position(Game.BOARD_SIZE - 1, Game.BOARD_SIZE - 1).isInside());
        }

        @Test
        @DisplayName("Posições com coordenadas negativas ou >= BOARD_SIZE estão fora")
        void outside() {
            assertFalse(new Position(-1, 0).isInside());
            assertFalse(new Position(0, -1).isInside());
            assertFalse(new Position(Game.BOARD_SIZE, 0).isInside());
            assertFalse(new Position(0, Game.BOARD_SIZE).isInside());
        }
    }

    @Nested
    @DisplayName("Adjacência")
    class Adjacency {

        @Test
        @DisplayName("Vizinhos horizontais, verticais e diagonais são adjacentes")
        void adjacent() {
            assertTrue(pos.isAdjacentTo(new Position(2, 4)));
            assertTrue(pos.isAdjacentTo(new Position(1, 3)));
            assertTrue(pos.isAdjacentTo(new Position(3, 4)));
        }

        @Test
        @DisplayName("Posições a 2 ou mais casas não são adjacentes")
        void notAdjacent() {
            assertFalse(pos.isAdjacentTo(new Position(2, 5)));
            assertFalse(pos.isAdjacentTo(new Position(4, 3)));
        }

        @Test
        @DisplayName("Uma posição no meio do tabuleiro tem 8 posições adjacentes")
        void eightNeighbours() {
            List<IPosition> adj = pos.adjacentPositions();
            assertEquals(8, adj.size());
            adj.forEach(p -> assertTrue(pos.isAdjacentTo(p)));
        }

        @Test
        @DisplayName("Uma posição no canto só tem 3 posições adjacentes (as restantes ficam fora)")
        void cornerHasThreeNeighbours() {
            List<IPosition> adj = new Position(0, 0).adjacentPositions();
            assertEquals(3, adj.size());
            adj.forEach(p -> assertTrue(p.isInside()));
        }
    }

    @Nested
    @DisplayName("Estado (ocupada / atingida)")
    class State {

        @Test
        @DisplayName("Uma posição nova não está ocupada nem atingida")
        void initialState() {
            assertFalse(pos.isOccupied());
            assertFalse(pos.isHit());
        }

        @Test
        @DisplayName("occupy marca a posição como ocupada")
        void occupy() {
            pos.occupy();
            assertTrue(pos.isOccupied());
            assertFalse(pos.isHit());
        }

        @Test
        @DisplayName("shoot marca a posição como atingida")
        void shoot() {
            pos.shoot();
            assertTrue(pos.isHit());
            assertFalse(pos.isOccupied());
        }
    }
}