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
    private GoalManager goalManager;
    private TrainManager trainManager;
    private Map map;
    private GameState state;
    private List<GameStateListener> gameStateListeners = new ArrayList<GameStateListener>();

    private Game() {
        playerManager = new PlayerManager();
        playerManager.createPlayers(CONFIG_PLAYERS);

        trainManager = new TrainManager();
        goalManager = new GoalManager(trainManager);

        map = new Map();

        state = GameState.NORMAL;

        playerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                for (Player currentPlayer : playerManager.getAllPlayers()) {
                    goalManager.addRandomGoalToPlayer(currentPlayer);
                    trainManager.addRandomTrainToPlayer(currentPlayer);
                    trainManager.addRandomTrainToPlayer(currentPlayer);
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
            goalManager.addRandomGoalToPlayer(player);
            trainManager.addRandomTrainToPlayer(player);
            trainManager.addRandomTrainToPlayer(player);
        }
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public TrainManager getTrainManager() {
        return trainManager;
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

    public void subscribeStateChanged(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    private void stateChanged() {
        for (GameStateListener listener : gameStateListeners) {
            listener.changed(state);
        }
    }
}
