package gamelogic.recording;

import gamelogic.goal.Goal;
import gamelogic.map.Position;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the state of the game at every turn
 *
 */
public class RecordState {
    private Integer turn;
    private Map<Train, Position> trainPositions = new HashMap<>();
    private Map<Integer, List<Goal>> playerGoals = new HashMap<>();
    private Map<Integer, List<Train>> playerTrains = new HashMap<>();
    private Map<Integer, Integer> playerMoney = new HashMap<>();
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

            Integer playerNumber = player.getPlayerNumber();

            playerGoals.put(playerNumber, player.getGoals());

            playerTrains.put(playerNumber, player.getTrains());

            playerMoney.put(playerNumber, player.getMoney());
        }
    }
}
