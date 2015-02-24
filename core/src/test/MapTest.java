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
        String name1 = "station1";
        String name2 = "station2";


        int previousSize = map.getStations().size();

        map.addStation(name1, new Position(9999, 9999));
        map.addStation(name2, new Position(200, 200));

        assertTrue("Failed to add stations", map.getStations().size() - previousSize == 2);

        map.addConnection(name1, name2, Connection.Material.GOLD);
        assertTrue("Connection addition failed", map.doesConnectionExist(name2, name1));

        // Should throw an error by itself
        map.getStationByPosition(new Position(9999, 9999));
    }

    @Test
    public void computeDistancesTest() throws Exception {
        Station london = map.getStationByName("London");
        Station paris = map.getStationByName("Paris");
        Station prague = map.getStationByName("Prague");

        assertEquals(0f, map.getShortestRouteDistance(london, london), 0.0001f);

        assertEquals(101.1187f, map.getShortestRouteDistance(london, paris), 0.0001f);
        assertEquals(101.1187f, map.getShortestRouteDistance(paris, london), 0.0001f);

        assertEquals(275.3817f, map.getShortestRouteDistance(london, prague), 0.0001f);
        assertEquals(174.2630f, map.getShortestRouteDistance(prague, paris), 0.0001f);

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
