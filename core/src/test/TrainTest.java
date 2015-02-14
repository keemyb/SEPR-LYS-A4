package test;


import gamelogic.map.Position;
import gamelogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TrainTest {
    Train train;

    @Before
    public void trainSetup() throws Exception {
        train = new Train("RedTrain", 250);
    }

    @Test
    public void finalDestinationTest() throws Error {
        Position station1 = new Position(5, 5);
        Position station2 = new Position(6, 6);
        ArrayList<Position> route = new ArrayList<>();
        route.add(station1);
        route.add(station2);

        train.setRoute(route);
        assertTrue("Setting a train route was not succesful", train.getRoute().size() == 2);
        assertTrue("Final destination wasn't set", train.getFinalPosition() == station2);
    }


}
