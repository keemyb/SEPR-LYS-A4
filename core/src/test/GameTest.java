package test;

import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GameTest extends LibGdxTest {
    private PlayerManager pm;

    @Before
    public void setUpGame() throws Exception {
        Game game = Game.getInstance();
    }

    @Test
    public void testInitialisePlayers() {
        Player currentPlayer = PlayerManager.getCurrentPlayer();

        // fresh players should start with at least 1 goal and resource
        assertTrue(currentPlayer.getTrains().size() > 0);
        assertTrue(currentPlayer.getGoals().size() > 0);
    }

    @Test
    public void testPlayerChanged() throws Exception {
        Player p1 = PlayerManager.getCurrentPlayer();
        int resourceCount = p1.getTrains().size();
        int goalCount = p1.getGoals().size();

        PlayerManager.turnOver();
        PlayerManager.turnOver();

        // resource count should increase when p1 has another turn
        assertTrue(p1.getTrains().size() > resourceCount);
        assertTrue(p1.getGoals().size() > goalCount);
    }
}