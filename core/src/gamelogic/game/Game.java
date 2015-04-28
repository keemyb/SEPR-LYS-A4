package gamelogic.game;

import gamelogic.goal.GoalManager;
import gamelogic.map.Map;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.replay.EventReplayer;
import gamelogic.resource.TrainManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton class represents game instance. Game has three phases determined by the number of turns completed.
 * Depending on the phase, different trains and goals are generated.
 */
public class Game {
    public static final char CURRENCY_SYMBOL = '$';
    public static final boolean CAN_PLAYER_PURCHASE_WITH_INSUFFICIENT_FUNDS = false;
    private static Game instance;
    private final int CONFIG_PLAYERS = 2;
    public int totalTurns = 30;
    private Map map;
    private GameState state;
    private List<GameStateListener> gameStateListeners = new ArrayList<>();

    private Game() {
        map = new Map();
        state = GameState.NORMAL;
        PlayerManager.createPlayers(CONFIG_PLAYERS);
        PlayerManager.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                givePlayersGoalAndTrains();
            }
        });
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
            instance.givePlayersGoalAndTrains(); // Can't be called in the constructor, as requires game instance to exist
        }
        return instance;
    }

    public void givePlayersGoalAndTrains() {
        if (EventReplayer.isReplaying()) return;

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
}
