package test;


import gamelogic.map.Position;
import gamelogic.map.Station;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StationTest {
    @Test
    public void stationsTest() throws Exception {
        int x = 5000;
        int y = 7000;
        String name = "TestStation";
        String abbreviation = "TestAbbreviation";

        Station testStation = new Station(name, abbreviation, new Position(x, y));

        assertTrue("Position is wrong", testStation.getLocation().getX() == x && testStation.getLocation().getY() == y);
        assertTrue("Name is wrong", testStation.getName().equals(name));
        assertTrue("Abbreviation is wrong", testStation.getAbbreviation().equals(abbreviation));
    }
}
