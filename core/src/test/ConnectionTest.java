package test;

import gamelogic.map.Connection;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionTest extends LibGdxTest {
    Station testStation1 = new Station("Test Station 1", new Position(0, 0));
    Station testStation2 = new Station("Test Station 2", new Position(10, 10));
    Station testStation3 = new Station("Test Station 3", new Position(20, 20));

    Train testTrain = new Train("Test Train", 100);

    Connection bronzeConnection;
    Connection silverConnection;
    Connection goldConnection;

    @Before
    public void setUp() {
        bronzeConnection = new Connection(testStation1, testStation2, Connection.Material.BRONZE);
        silverConnection = new Connection(testStation1, testStation2, Connection.Material.SILVER);
        goldConnection = new Connection(testStation1, testStation2, Connection.Material.GOLD);
    }

    @Test
    public void testUpgradeConnectionBronze() {
        assertFalse(bronzeConnection.isUpgradable(Connection.Material.BRONZE));
        assertTrue(bronzeConnection.isUpgradable(Connection.Material.SILVER));
        assertTrue(bronzeConnection.isUpgradable(Connection.Material.GOLD));

        bronzeConnection.upgrade(Connection.Material.SILVER);
        assertEquals(Connection.Material.SILVER.getColour() ,bronzeConnection.getColour());
    }

    @Test
    public void testUpgradeConnectionSilver() {
        assertFalse(silverConnection.isUpgradable(Connection.Material.BRONZE));
        assertFalse(silverConnection.isUpgradable(Connection.Material.SILVER));
        assertTrue(silverConnection.isUpgradable(Connection.Material.GOLD));

        silverConnection.upgrade(Connection.Material.GOLD);
        assertEquals(Connection.Material.GOLD.getColour() ,silverConnection.getColour());
    }

    @Test
    public void testUpgradeConnectionGold() {
        assertFalse(goldConnection.isUpgradable(Connection.Material.BRONZE));
        assertFalse(goldConnection.isUpgradable(Connection.Material.SILVER));
        assertFalse(goldConnection.isUpgradable(Connection.Material.GOLD));

        goldConnection.upgrade(Connection.Material.SILVER);
        assertEquals(Connection.Material.GOLD.getColour() ,goldConnection.getColour());
    }

    @Test
    public void testDamageConnectionBronze() {
        float currentHealth = bronzeConnection.getHealth();

        bronzeConnection.inflictDamage(testTrain);
        assertTrue(currentHealth > bronzeConnection.getHealth());
    }

    @Test
    public void testDamageConnectionSilver() {
        float currentHealth = silverConnection.getHealth();

        silverConnection.inflictDamage(testTrain);
        assertTrue(currentHealth > silverConnection.getHealth());
    }

    @Test
    public void testDamageConnectionGold() {
        float currentHealth = goldConnection.getHealth();

        goldConnection.inflictDamage(testTrain);
        assertEquals(currentHealth, goldConnection.getHealth(), 0.0f);
    }

    @Test
    public void testRepairConnection() {
        bronzeConnection.inflictDamage(testTrain);
        bronzeConnection.repair(1);

        assertEquals(1, bronzeConnection.getHealth(), 0.0f);
    }

    @Test
    public void testUpgradeDamagedConnection() {
        bronzeConnection.inflictDamage(testTrain);

        bronzeConnection.upgrade(Connection.Material.SILVER);
        assertEquals(1, bronzeConnection.getHealth(), 0.0f);
    }

    @Test
    public void testAdjustedTrainSpeedHealthyConnection() {
        int expectedSpeed = testTrain.getSpeed();
        int adjustedSpeed = bronzeConnection.calculateAdjustedTrainSpeed(testTrain);

        assertEquals(expectedSpeed, adjustedSpeed);
    }

    @Test
    public void testAdjustedTrainSpeedDamagedConnection() {
        int normalSpeed = testTrain.getSpeed();

        bronzeConnection.inflictDamage(testTrain);
        int adjustedSpeed = bronzeConnection.calculateAdjustedTrainSpeed(testTrain);

        assertTrue(normalSpeed > adjustedSpeed);
    }

    @Test
    public void testGetRentPayableVariableDistance() {
        Connection longerConnection = new Connection(testStation1, testStation3, Connection.Material.BRONZE);

        int shortConnectionCost = bronzeConnection.getRentPayable();
        int longConnectionCost = longerConnection.getRentPayable();

        assertTrue(shortConnectionCost > 0);
        assertTrue(longConnectionCost > shortConnectionCost);
    }

    @Test
    public void testGetRentPayableVariableMaterial() {
        int bronzeConnectionCost = bronzeConnection.getRentPayable();
        int silverConnectionCost = silverConnection.getRentPayable();
        int goldConnectionCost = goldConnection.getRentPayable();

        assertTrue(bronzeConnectionCost > 0);
        assertTrue(silverConnectionCost > bronzeConnectionCost);
        assertTrue(goldConnectionCost > silverConnectionCost);
    }
}