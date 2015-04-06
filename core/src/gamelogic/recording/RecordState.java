package gamelogic.recording;

import gamelogic.goal.Goal;
import gamelogic.map.Connection;
import gamelogic.map.Position;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores the state of the game at every turn
 *
 */
public class RecordState {
    private Integer turn;
    private Map<Train, Position> trainPositions = new HashMap<>();
    private Map<Player, List<Goal>> playerGoals = new HashMap<>();
    private Map<Player, List<Train>> playerTrains = new HashMap<>();
    private Map<Player, Set<Connection>> playerConnections = new HashMap<>();
    private Map<Player, Integer> playerMoney = new HashMap<>();
    private Long delta;

    RecordState(Long delta) {
        this.delta = delta;
        //TODO save turn number

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getTrains()) {
                if (!(resource instanceof Train)) continue;
                Train train = (Train) resource;
                trainPositions.put(train, train.getPosition());
            }

            playerGoals.put(player, player.getGoals());

            playerTrains.put(player, player.getTrains());

            playerConnections.put(player, player.getConnectionsOwned());

            playerMoney.put(player, player.getMoney());
        }
    }
}
