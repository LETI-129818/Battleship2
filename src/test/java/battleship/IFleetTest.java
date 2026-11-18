
package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for GameDatabase.
 *
 *  * Author: LETI-123856
 *  * Date: 12/11/2026 12:30
 *
 * */

class IFleetTest {

    @Test
    @DisplayName("Fleet size should be 11")
    void testFleetSize() {
        assertEquals(11, IFleet.FLEET_SIZE);
    }

    @Test
    @DisplayName("Fleet should implement IFleet")
    void testFleetImplementsIFleet() {
        assertTrue(IFleet.class.isAssignableFrom(Fleet.class));
    }

    @Test
    @DisplayName("Fleet should be usable through IFleet")
    void testFleetThroughInterface() {
        IFleet fleet = new Fleet();

        assertNotNull(fleet);
        assertNotNull(fleet.getShips());
        assertTrue(fleet.getShips().isEmpty());
    }
}
