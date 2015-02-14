package test;

import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.TrainManager;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TrainManagerTest extends LibGdxTest {
    @Test
    public void testAddTrainToPlayer() throws Exception {
        Player player = new Player(1);

        // add enough resources to exceed maximum
        for (int i = 0; i < 20; i++) {
            TrainManager.addRandomTrainToPlayer(player);
        }

        assertTrue(player.getResources().size() == TrainManager.CONFIG_MAX_TRAINS);
    }
}