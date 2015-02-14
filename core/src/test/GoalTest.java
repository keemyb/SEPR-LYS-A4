package test;


import gamelogic.Game;
import gamelogic.Player;
import gamelogic.PlayerManager;
import gamelogic.goal.Goal;
import gamelogic.goal.GoalManager;
import gamelogic.map.Map;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GoalTest extends LibGdxTest {
    PlayerManager pm;
    Game game;

    @Before
    public void setup() throws Exception {
        game = Game.getInstance();
        pm = game.getPlayerManager();
    }

    @Test
    public void goalTest() throws Exception {
        pm.createPlayers(2);

        Station station1 = game.getMap().getStationByName("London");
        Station station2 = game.getMap().getStationByName("Paris");

        Goal goal = new Goal(station1, station2, pm.getTurnNumber());
        pm.getCurrentPlayer().addGoal(goal);

        goal.addTurnLimitConstraint(20);
        assertEquals(20, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(20, goal.getTurnLimit());

        pm.turnOver();
        assertEquals(20, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(20, goal.getTurnLimit());

        pm.turnOver();
        assertEquals(19, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(18, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(17, goal.getTurnLimit());
    }
}
