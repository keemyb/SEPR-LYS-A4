package test;

import gamelogic.Player;
import gamelogic.PlayerManager;
import gamelogic.resource.TrainManager;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TrainManagerTest extends LibGdxTest {
    @Test
    public void testAddTrainToPlayer() throws Exception {
        PlayerManager pm = new PlayerManager();
        Player player = new Player(pm, 1);
        TrainManager rm = new TrainManager();

        // add enough resources to exceed maximum
        for (int i = 0; i < 20; i++) {
            rm.addRandomTrainToPlayer(player);
        }

        assertTrue(player.getResources().size() == rm.CONFIG_MAX_TRAINS);
    }
}