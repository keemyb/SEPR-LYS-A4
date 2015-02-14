package gamelogic;

import gamelogic.goal.GoalManager;
import gamelogic.map.Map;
import gamelogic.resource.TrainManager;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static Game instance;
    public int totalTurns = 30;
    private final int CONFIG_PLAYERS = 2;
    private PlayerManager playerManager;

    private Map map;
    private GameState state;
    private List<GameStateListener> gameStateListeners = new ArrayList<GameStateListener>();

    private Game() {
        playerManager = new PlayerManager();
        playerManager.createPlayers(CONFIG_PLAYERS);

        //trainManager = new TrainManager();

        map = new Map();

        state = GameState.NORMAL;

        playerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                for (Player currentPlayer : playerManager.getAllPlayers()) {
                    GoalManager.addRandomGoalToPlayer(currentPlayer);
                    TrainManager.addRandomTrainToPlayer(currentPlayer);
                    TrainManager.addRandomTrainToPlayer(currentPlayer);
                }
            }
        });
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
            // initialisePlayers gives them a goal, and the GoalManager requires an instance of game to exist so this
            // method can't be called in the constructor
            instance.initialisePlayers();
        }

        return instance;
    }

    private void initialisePlayers() {
        for (Player player : playerManager.getAllPlayers()) {
            GoalManager.addRandomGoalToPlayer(player);
            TrainManager.addRandomTrainToPlayer(player);
            TrainManager.addRandomTrainToPlayer(player);
        }
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Map getMap() {
        return map;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        stateChanged();
    }

    public int getPhase() {
        return (int) Math.floor((playerManager.getTurnNumber() / Game.getInstance().totalTurns) * 3.0);
    }

    public void subscribeStateChanged(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    private void stateChanged() {
        for (GameStateListener listener : gameStateListeners) {
            listener.changed(state);
        }
    }
}
