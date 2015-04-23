package test;


import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StationTest {
    @Test
    public void stationsTest() throws Exception {
        int x = 5000;
        int y = 7000;
        String name = "TestStation";


        Station testStation = new Station(name, new Position(x, y));

        assertTrue("Position is wrong", testStation.getLocation().getX() == x && testStation.getLocation().getY() == y);
        assertTrue("Name is wrong", testStation.getName().equals(name));
    }

    @Test
    public void computeDistancesTest() throws Exception {
        Station station1 = new Station("Station 1", new Position(0, 0));
        Station station2 = new Station("Station 2", new Position(1, 1));

        assertEquals(Math.sqrt(2), Station.getDistance(station1, station2), 0.0001f);
    }
}
