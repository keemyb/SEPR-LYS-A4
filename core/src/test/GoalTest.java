package test;


import gamelogic.game.Game;
import gamelogic.player.PlayerManager;
import gamelogic.goal.Goal;
import gamelogic.map.Station;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GoalTest extends LibGdxTest {
    Game game;

    @Before
    public void setup() throws Exception {
        game = Game.getInstance();
    }

    @Test
    public void goalTest() throws Exception {

        Station station1 = game.getMap().getStationByName("London");
        Station station2 = game.getMap().getStationByName("Paris");

        PlayerManager.clearAllGoals();
        Goal goal = new Goal(station1, station2, PlayerManager.getTurnNumber());
        PlayerManager.getCurrentPlayer().addGoal(goal);

        goal.addTurnLimitConstraint(20);
        assertEquals(20, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(20, goal.getTurnLimit());

        PlayerManager.turnOver();
        assertEquals(20, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(20, goal.getTurnLimit());

        PlayerManager.turnOver();
        assertEquals(19, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(18, goal.getTurnLimit());
        goal.updateGoal();
        assertEquals(17, goal.getTurnLimit());
    }
}
