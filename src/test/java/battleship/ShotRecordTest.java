
package battleship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ShotRecordTest {

    @Test
    @DisplayName("Verificar construtor vazio")
    void testEmptyConstructor() {

        ShotRecord record = new ShotRecord();

        assertEquals(0, record.getRow());
        assertEquals(0, record.getColumn());
        assertFalse(record.isHit());
        assertNull(record.getShipCategory());
    }

    @Test
    @DisplayName("Verificar construtor com parâmetros")
    void testConstructorWithParameters() {

        ShotRecord record = new ShotRecord(2, 3, true, "Barca");

        assertEquals(2, record.getRow());
        assertEquals(3, record.getColumn());
        assertTrue(record.isHit());
        assertEquals("Barca", record.getShipCategory());
    }

    @Test
    @DisplayName("Verificar getRow")
    void getRow() {

        ShotRecord record = new ShotRecord(4, 2, false, null);

        assertEquals(4, record.getRow());
    }

    @Test
    @DisplayName("Verificar setRow")
    void setRow() {

        ShotRecord record = new ShotRecord();

        record.setRow(5);

        assertEquals(5, record.getRow());
    }

    @Test
    @DisplayName("Verificar getColumn")
    void getColumn() {

        ShotRecord record = new ShotRecord(2, 6, false, null);

        assertEquals(6, record.getColumn());
    }

    @Test
    @DisplayName("Verificar setColumn")
    void setColumn() {

        ShotRecord record = new ShotRecord();

        record.setColumn(7);

        assertEquals(7, record.getColumn());
    }

    @Test
    @DisplayName("Verificar isHit")
    void isHit() {

        ShotRecord record = new ShotRecord(1, 2, true, "Barca");

        assertTrue(record.isHit());
    }

    @Test
    @DisplayName("Verificar setHit")
    void setHit() {

        ShotRecord record = new ShotRecord();

        record.setHit(true);

        assertTrue(record.isHit());

        record.setHit(false);

        assertFalse(record.isHit());
    }

    @Test
    @DisplayName("Verificar getShipCategory")
    void getShipCategory() {

        ShotRecord record = new ShotRecord(1, 2, true, "Barca");

        assertEquals("Barca", record.getShipCategory());
    }

    @Test
    @DisplayName("Verificar setShipCategory")
    void setShipCategory() {

        ShotRecord record = new ShotRecord();

        record.setShipCategory("Barca");

        assertEquals("Barca", record.getShipCategory());
    }
}
