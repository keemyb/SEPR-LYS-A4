package test;

import gamelogic.map.Connection;
import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionTest {
    Station testStation1 = new Station("Test Station 1", new Position(0, 0));
    Station testStation2 = new Station("Test Station 2", new Position(1, 1));

    @Test
    public void testUpgradeConnectionBronze() {
        Connection bronzeConnection = new Connection(testStation1, testStation2, Connection.Material.BRONZE);

        assertFalse(bronzeConnection.isUpgradable(Connection.Material.BRONZE));
        assertTrue(bronzeConnection.isUpgradable(Connection.Material.SILVER));
        assertTrue(bronzeConnection.isUpgradable(Connection.Material.GOLD));

        bronzeConnection.upgrade(Connection.Material.SILVER);
        assertEquals(Connection.Material.SILVER.getColour() ,bronzeConnection.getColour());
    }

    @Test
    public void testUpgradeConnectionSilver() {
        Connection silverConnection = new Connection(testStation1, testStation2, Connection.Material.SILVER);

        assertFalse(silverConnection.isUpgradable(Connection.Material.BRONZE));
        assertFalse(silverConnection.isUpgradable(Connection.Material.SILVER));
        assertTrue(silverConnection.isUpgradable(Connection.Material.GOLD));

        silverConnection.upgrade(Connection.Material.GOLD);
        assertEquals(Connection.Material.GOLD.getColour() ,silverConnection.getColour());
    }

    @Test
    public void testUpgradeConnectionGold() {
        Connection goldConnection = new Connection(testStation1, testStation2, Connection.Material.GOLD);

        assertFalse(goldConnection.isUpgradable(Connection.Material.BRONZE));
        assertFalse(goldConnection.isUpgradable(Connection.Material.SILVER));
        assertFalse(goldConnection.isUpgradable(Connection.Material.GOLD));

        goldConnection.upgrade(Connection.Material.SILVER);
        assertEquals(Connection.Material.GOLD.getColour() ,goldConnection.getColour());
    }
}