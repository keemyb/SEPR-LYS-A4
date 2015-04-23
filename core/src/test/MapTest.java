package test;

import gamelogic.map.Connection;
import gamelogic.map.Map;
import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
        Map testMap = new Map();

        Station start = new Station("Station 1", new Position(0, 0));
        Station shorter = new Station("Station 2", new Position(1, 1));
        Station longer = new Station("Station 2", new Position(1.1f, 1.1f));
        Station end = new Station("Station 3", new Position(2, 2));

        testMap.getStations().clear();
        testMap.addStation(start);
        testMap.addStation(shorter);
        testMap.addStation(longer);
        testMap.addStation(end);

        Connection shortPart1 = new Connection(start, shorter, Connection.Material.GOLD);
        Connection shortPart2 = new Connection(shorter, end, Connection.Material.GOLD);
        Connection longPart1 = new Connection(start, longer, Connection.Material.GOLD);
        Connection longPart2 = new Connection(longer, end, Connection.Material.GOLD);

        testMap.getConnections().clear();
        testMap.addConnection(shortPart1);
        testMap.addConnection(shortPart2);
        testMap.addConnection(longPart1);
        testMap.addConnection(longPart2);

        assertEquals(2 * Math.sqrt(2), testMap.getLengthOfShortestRoute(start, end), 0.0001f);
    }

    @Test
    public void connectionIntersectionTest() throws Exception {
        Map testMap = new Map();

        Station a1 = new Station("a1", new Position(0, 0));
        Station a2 = new Station("a2", new Position(1, 1));
        Connection nonOverlappingConnection = new Connection(a1, a2, Connection.Material.GOLD);

        Station b1 = new Station("b1", new Position(2, 2));
        Station b2 = new Station("b2", new Position(3, 3));
        Connection overlappingConnection1 = new Connection(b1, b2, Connection.Material.GOLD);

        Station c1 = new Station("c1", new Position(2, 3));
        Station c2 = new Station("c2", new Position(3, 2));
        Connection overlappingConnection2 = new Connection(c1, c2, Connection.Material.GOLD);

        // Not clearing this may give false positives with the json stations/connections
        testMap.getStations().clear();

        testMap.getConnections().clear();
        testMap.addConnection(nonOverlappingConnection);
        testMap.addConnection(overlappingConnection1);
        testMap.addConnection(overlappingConnection2);

        assertFalse(testMap.doesProspectiveConnectionIntersectExisting(nonOverlappingConnection));
        assertTrue(testMap.doesProspectiveConnectionIntersectExisting(overlappingConnection1));
        assertTrue(testMap.doesProspectiveConnectionIntersectExisting(overlappingConnection2));
    }
}
