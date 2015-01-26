package test;


import gamelogic.Player;
import gamelogic.PlayerManager;
import gamelogic.goal.Goal;
import gamelogic.goal.GoalManager;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.resource.ResourceManager;
import gamelogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class GoalManagerTest extends LibGdxTest {
    GoalManager gm;
    PlayerManager pm;

    @Before
    public void setup() throws Exception {
        ResourceManager rs = new ResourceManager();
        gm = new GoalManager(rs);
        pm = new PlayerManager();

    }

    @Test
    public void goalManagerTest() throws Exception {
        pm.createPlayers(2);
        Player player1 = pm.getCurrentPlayer();

        Train train = new Train("Green", "", "", 100);

        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(2, 2));

        Goal goal = new Goal(station1, station2, 0);
        player1.addGoal(goal);
        player1.addResource(train);

        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);
        train.setRoute(route);

        train.addHistory("station1", 0);

        pm.turnOver();
        pm.turnOver();
        train.addHistory("station2", 1);

        ArrayList<String> completedStrings = gm.trainArrived(train, player1);
        assertTrue("Goal wasn't completed", goal.isComplete(train));
        assertTrue("Completed goal string not right", completedStrings.size() > 0);

    }
}
