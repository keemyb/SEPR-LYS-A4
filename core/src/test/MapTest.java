package test;

import gamelogic.map.Connection;
import gamelogic.map.Map;
import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapTest extends LibGdxTest {
    private Map map;

    @Before
    public void mapSetup() throws Exception {
        map = new Map();
    }

    @Test
    public void addStationAndConnectionTest() throws Exception {
        Station station1 = new Station("Station 1", new Position(9999, 9999));
        Station station2 = new Station("Station 2", new Position(200, 200));
        Connection connection = new Connection(station1, station2, Connection.Material.GOLD);

        int previousSize = map.getStations().size();

        map.addStation(station1);
        map.addStation(station2);

        assertTrue("Failed to add stations", map.getStations().size() - previousSize == 2);

        map.addConnection(connection);
        assertTrue("Connection addition failed", map.doesConnectionExist(station1, station2));

        // Should throw an error by itself
        map.getStationByPosition(new Position(9999, 9999));
    }

    @Test
    public void computeDistancesTest() throws Exception {
        Station london = map.getStationByName("London");
        Station paris = map.getStationByName("Paris");
        Station prague = map.getStationByName("Prague");

        assertEquals(0f, map.getLengthOfShortestRoute(london, london), 0.0001f);

        assertEquals(101.1187f, map.getLengthOfShortestRoute(london, paris), 0.0001f);
        assertEquals(101.1187f, map.getLengthOfShortestRoute(paris, london), 0.0001f);

        assertEquals(275.3817f, map.getLengthOfShortestRoute(london, prague), 0.0001f);
        assertEquals(174.2630f, map.getLengthOfShortestRoute(prague, paris), 0.0001f);

    }

    @Test
    public void connectionIntersectionTest() throws Exception {
        Station paris = map.getStationByName("Paris");
        Station berlin = map.getStationByName("Berlin");
        Connection overlappingConnection = new Connection(paris, berlin, Connection.Material.GOLD);

        Station madrid = map.getStationByName("Madrid");
        Station rome = map.getStationByName("Rome");
        Connection nonOverlappingConnection = new Connection(madrid, rome, Connection.Material.GOLD);

        assertTrue(map.doesProspectiveConnectionIntersectExisting(overlappingConnection));
        assertFalse(map.doesProspectiveConnectionIntersectExisting(nonOverlappingConnection));
    }
}
