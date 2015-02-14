package test;


import gamelogic.Game;
import gamelogic.Player;
import gamelogic.PlayerManager;
import gamelogic.goal.Goal;
import gamelogic.goal.GoalManager;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GoalManagerTest extends LibGdxTest {
    Game game;
    PlayerManager pm;
    GoalManager gm;

    @Before
    public void setup() throws Exception {
        game = Game.getInstance();
        pm = game.getPlayerManager();
    }

    @Test
    public void goalManagerTest() throws Exception {
        //pm.createPlayers(2);
        Player player1 = pm.getCurrentPlayer();

        Train train = new Train("Green", 100);

//        Station station1 = new Station("station1", new Position(5, 5));
//        Station station2 = new Station("station2", new Position(2, 2));

        Station station1 = game.getMap().getStationByName("London");
        Station station2 = game.getMap().getStationByName("Paris");

        Goal goal = new Goal(station1, station2, 0);
        player1.addGoal(goal);
        player1.addResource(train);

        ArrayList<Position> route = new ArrayList<>();
        route.add(station1.getLocation());
        route.add(station2.getLocation());
        train.setRoute(route);

        train.addToHistory("London", 0);

        pm.turnOver();
        pm.turnOver();
        train.addToHistory("Paris", 1);

        ArrayList<String> completedStrings = gm.trainArrived(train, player1);
        assertTrue("Goal wasn't completed", goal.isComplete(train));
        assertTrue("Completed goal string not right", completedStrings.size() > 0);

    }
}
