package test;

import gamelogic.map.Map;
import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
        String abbreviation1 = "abbreviation1";
        String abbreviation2 = "abbreviation2";

        int previousSize = map.getStations().size();

        map.addStation(name1, abbreviation1, new Position(9999, 9999));
        map.addStation(name2, abbreviation2, new Position(200, 200));

        assertTrue("Failed to add stations", map.getStations().size() - previousSize == 2);

        map.addConnection(name1, name2);
        assertTrue("Connection addition failed", map.doesConnectionExist(name2, name1));

        // Should throw an error by itself
        map.getStationFromPosition(new Position(9999, 9999));
    }

    @Test
    public void computeDistancesTest() throws Exception {
        Station london = map.getStationByName("London");
        Station paris = map.getStationByName("Paris");
        Station prague = map.getStationByName("Prague");

        assertEquals(0f, map.getDistance(london, london), 0.0001f);

        assertEquals(101.1187f, map.getDistance(london, paris), 0.0001f);
        assertEquals(101.1187f, map.getDistance(paris, london), 0.0001f);

        assertEquals(275.3817f, map.getDistance(london, prague), 0.0001f);
        assertEquals(174.2630f, map.getDistance(prague, paris), 0.0001f);

    }
}
