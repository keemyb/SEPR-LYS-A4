package gamelogic.game;

import gamelogic.goal.GoalManager;
import gamelogic.map.Map;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.recording.RecordState;
import gamelogic.recording.RecordStateManager;
import gamelogic.resource.TrainManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton class represents game instance. Game has three phases determined by the number of turns completed.
 * Depending on the phase, different trains and goals are generated.
 */
public class Game {
    public static final char CURRENCY_SYMBOL = '$';
    public static final boolean CAN_PLAYER_PURCHASE_WITH_NEGATIVE_FUNDS = true;
    private static Game instance;
    private final int CONFIG_PLAYERS = 2;
    public int totalTurns = 30;
    private Map map;
    private GameState state;
    private List<GameStateListener> gameStateListeners = new ArrayList<>();

    private RecordStateManager recordStateManager;
    private List<RecordState> recordStates;
    private RecordState currentRecordState;
    private RecordState nextRecordState;

    private Game() {
        map = new Map();
        recordStateManager = new RecordStateManager(this);
        state = GameState.NORMAL;
        PlayerManager.createPlayers(CONFIG_PLAYERS);
        PlayerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                for (Player player : PlayerManager.getAllPlayers()) {
                    GoalManager.addRandomGoalToPlayer(player);
                    TrainManager.addRandomTrainToPlayer(player);
                    TrainManager.addRandomTrainToPlayer(player);
                }
            }
        });
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
            instance.initialisePlayers(); // Can't be called in the constructor, as requires game instance to exist
        }
        return instance;
    }

    private void initialisePlayers() {
        for (Player player : PlayerManager.getAllPlayers()) {
            GoalManager.addRandomGoalToPlayer(player);
            TrainManager.addRandomTrainToPlayer(player);
            TrainManager.addRandomTrainToPlayer(player);
        }
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public Map getMap() {
        return map;
    }

    public GameState getState() {
        return state;
    }

    public RecordStateManager getRecordStateManager() {
        return recordStateManager;
    }

    public void setState(GameState state) {
        this.state = state;
        stateChanged();
    }

    public int getPhase() {
        return (int) Math.floor(((float) PlayerManager.getTurnNumber() / (float) Game.getInstance().totalTurns) * 3.0);
    }

    public void subscribeStateChanged(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    private void stateChanged() {
        for (GameStateListener listener : gameStateListeners) {
            listener.changed(state);
        }
    }

    public void startReplay() {
        recordStates = recordStateManager.getRecordStates();
        nextRecordState = recordStates.get(0);

        System.out.println("Start Replay");

        advanceReplay();
    }

    public void advanceReplay() {
        if (nextRecordState == null) return;

        RecordState previousRecordState = currentRecordState;
        currentRecordState = nextRecordState;
        nextRecordState = recordStates.get(recordStates.indexOf(currentRecordState) + 1);

        if (nextRecordState == null) return;

        if (previousRecordState == null
                || currentRecordState.getTurn() > previousRecordState.getTurn()) {
            showNewTurnRecordState();
        } else {
            showSameTurnRecordState();
        }
    }

    private void showNewTurnRecordState() {
        // Keyframe State stub
        System.out.println("Replay Keyframe");
    }

    private void showSameTurnRecordState() {
        // Interpolation state stub
        System.out.println("Replay Interpolation");
    }
}
