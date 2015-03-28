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
    Connection longerConnection;

    @Before
    public void setUp() {
        bronzeConnection = new Connection(testStation1, testStation2, Connection.Material.BRONZE);
        silverConnection = new Connection(testStation1, testStation2, Connection.Material.SILVER);
        goldConnection = new Connection(testStation1, testStation2, Connection.Material.GOLD);
        longerConnection = new Connection(testStation1, testStation3, Connection.Material.BRONZE);

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

    @Test
    public void testConnectionCostVariableDistance() {
        int shortConnectionCost = bronzeConnection.calculateCost();
        int longConnectionCost = longerConnection.calculateCost();

        assertTrue(shortConnectionCost > 0);
        assertTrue(longConnectionCost > shortConnectionCost);
    }

    @Test
    public void testConnectionCostVariableMaterial() {
        int bronzeConnectionCost = bronzeConnection.calculateCost();
        int silverConnectionCost = silverConnection.calculateCost();
        int goldConnectionCost = goldConnection.calculateCost();

        assertTrue(bronzeConnectionCost > 0);
        assertTrue(silverConnectionCost > bronzeConnectionCost);
        assertTrue(goldConnectionCost > silverConnectionCost);
    }

    @Test
    public void testRepairCostVariableDistance() {
        bronzeConnection.inflictDamage(testTrain);
        longerConnection.inflictDamage(testTrain);

        int shortConnectionCost = bronzeConnection.calculateRepairCost(1);
        int longConnectionCost = longerConnection.calculateRepairCost(1);

        assertTrue(shortConnectionCost > 0);
        assertTrue(longConnectionCost > shortConnectionCost);
    }

    @Test
    public void testRepairCostVariableMaterial() {
        // Making sure that silver and bronze connections have no health,
        // for a fair comparison.
        do {
            bronzeConnection.inflictDamage(testTrain);
            silverConnection.inflictDamage(testTrain);
            goldConnection.inflictDamage(testTrain);
        } while (silverConnection.getHealth() > 0);

        int bronzeConnectionCost = bronzeConnection.calculateRepairCost(1);
        int silverConnectionCost = silverConnection.calculateRepairCost(1);
        int goldConnectionCost = goldConnection.calculateRepairCost(1);

        assertTrue(bronzeConnectionCost > 0);
        assertTrue(silverConnectionCost > bronzeConnectionCost);
        assertEquals(0, goldConnectionCost);
    }

    @Test
    public void testUpgradeCostVariableDistance() {
        int shortConnectionCost = bronzeConnection.calculateUpgradeCost(Connection.Material.GOLD);
        int longConnectionCost = longerConnection.calculateUpgradeCost(Connection.Material.GOLD);

        assertTrue(shortConnectionCost > 0);
        assertTrue(longConnectionCost > shortConnectionCost);
    }

    @Test
    public void testUpgradeCostVariableMaterial() {
        int bronzeConnectionCost = bronzeConnection.calculateUpgradeCost(Connection.Material.GOLD);
        int silverConnectionCost = silverConnection.calculateUpgradeCost(Connection.Material.GOLD);
        int goldConnectionCost = goldConnection.calculateUpgradeCost(Connection.Material.GOLD);

        assertTrue(silverConnectionCost > 0);
        assertTrue(bronzeConnectionCost > silverConnectionCost);
        assertTrue(goldConnectionCost == 0);
    }
}