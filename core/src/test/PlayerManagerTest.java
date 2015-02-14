package test;

import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerManagerTest {
    private Game game;

    @Before
    public void setUp() throws Exception {
        Game game = Game.getInstance();
    }

    @Test
    public void testGetCurrentPlayer() throws Exception {
        Player p1 = PlayerManager.getCurrentPlayer();
        PlayerManager.turnOver();

        // player should change after PlayerManager.turnOver() is called
        assertFalse(p1.equals(PlayerManager.getCurrentPlayer()));
    }

    @Test
    public void testTurnNumber() throws Exception {
        int previous = PlayerManager.getTurnNumber();
        PlayerManager.turnOver();
        assertTrue("Turn number changed", previous == PlayerManager.getTurnNumber());
        PlayerManager.turnOver();
        assertTrue("Turn number did not change", previous < PlayerManager.getTurnNumber());
    }


}