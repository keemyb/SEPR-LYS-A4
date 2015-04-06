package gamelogic.recording;

import gamelogic.goal.Goal;
import gamelogic.map.*;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.*;
import java.util.Map;

/**
 * Stores the state of the game at every turn
 *
 */
public class RecordState {
    private int turn;
    private List<Connection> connections;
    private Map<Train, Position> trainPositions = new HashMap<>();
    private Map<Player, List<Goal>> playerGoals = new HashMap<>();
    private Map<Player, List<Train>> playerTrains = new HashMap<>();
    private Map<Player, Set<Connection>> playerConnections = new HashMap<>();
    private Map<Player, Integer> playerMoney = new HashMap<>();
    private Long delta;

    RecordState(Long delta, gamelogic.map.Map map) {
        this.delta = delta;

        turn = PlayerManager.getTurnNumber();

        connections = map.getConnections();

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

    public void restorePlayerAttributes() {
        for (Map.Entry entry : playerGoals.entrySet()) {
            Player player = (Player) entry.getKey();
            List<Goal> goals = (List<Goal>) entry.getValue();

            player.setGoals(goals);
        }

        for (Map.Entry entry : playerTrains.entrySet()) {
            Player player = (Player) entry.getKey();
            List<Train> trains = (List<Train>) entry.getValue();

            player.setTrains(trains);
        }

        for (Map.Entry entry : playerConnections.entrySet()) {
            Player player = (Player) entry.getKey();
            Set<Connection> connections = (Set<Connection>) entry.getValue();

            player.setConnectionsOwned(connections);
        }

        for (Map.Entry entry : playerMoney.entrySet()) {
            Player player = (Player) entry.getKey();
            int money = (int) entry.getValue();

            player.setMoney(money);
        }
    }

    public int getTurn() {
        return turn;
    }
}
