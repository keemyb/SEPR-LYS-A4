package gamelogic.player;

import gamelogic.game.Game;
import gamelogic.game.TurnListener;
import gamelogic.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerManager {

    private static ArrayList<Player> players = new ArrayList<>();
    private static int startingPlayer = 0; // The player who goes first this turn
    private static int currentPlayerNumber = 0;
    private static int turnNumber = 0;
    private static List<TurnListener> turnListeners = new ArrayList<>();
    private static List<PlayerChangedListener> playerListeners = new ArrayList<>();
    private static List<CurrentPlayerChangedListener> currentPlayerListeners = new ArrayList<>();

    public static void reset() {
        startingPlayer = 0;
        currentPlayerNumber = 0;
        turnNumber = 0;

        playerChanged();
    }

    public static void createPlayers(int count) {
        for (int i = 0; i < count; i++) {
            players.add(new Player(i + 1));
        }
    }

    public static Player getCurrentPlayer() {
        return players.get(currentPlayerNumber);
    }

    public static List<Player> getAllPlayers() {
        return players;
    }

    public static void turnOver() {
        currentPlayerChanged();
        if (currentPlayerNumber == startingPlayer) {
            currentPlayerNumber = startingPlayer == 0 ? 1 : 0;
            playerChanged();
        } else {
            startingPlayer = currentPlayerNumber;
            turnChanged();
        }
    }

    public static void subscribeTurnChanged(TurnListener listener) {
        turnListeners.add(listener);
    }

    public static void turnChanged() {
        if (turnNumber < Game.getInstance().totalTurns)
            turnNumber++;

        List<Goal> goalsToRemove = new ArrayList<>();
        for (Player player : players) {
            // Giving players a small top-up.
            player.spendMoney(-Player.MONEY_PER_TURN);
            for (Goal goal : player.getGoals()) {
                goal.updateGoal();
                if (goal.isExpired())
                    goalsToRemove.add(goal);
            }
            for (Goal goal : goalsToRemove)
                player.getGoals().remove(goal);
        }
        for (TurnListener listener : turnListeners) {
            listener.changed();
        }
    }

    public static void subscribeCurrentPlayerChanged(CurrentPlayerChangedListener listener) {
        currentPlayerListeners.add(listener);
    }

    /**
     * A general event which is fired when player's goals / resources are changed
     */
    public static void currentPlayerChanged() {
        for (CurrentPlayerChangedListener listener : currentPlayerListeners) {
            listener.changed();
        }
    }

    public static void subscribePlayerChanged(PlayerChangedListener listener) {
        playerListeners.add(listener);
    }

    /**
     * A general event which is fired when player's goals / resources are changed
     */
    public static void playerChanged() {
        for (PlayerChangedListener listener : playerListeners) {
            listener.changed();
        }
    }

    public static void clearAllGoals() {
        for (Player player: players)
            player.getGoals().clear();
    }

    public static int getTurnNumber() {
        return turnNumber;
    }
}
