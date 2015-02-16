package gamelogic.player;

import gamelogic.game.Game;
import gamelogic.game.TurnListener;
import gamelogic.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerManager {

    private static ArrayList<Player> players = new ArrayList<>();
    private static int currentPlayerNumber = 0;
    private static int turnNumber = 0;
    private static List<TurnListener> turnListeners = new ArrayList<>();
    private static List<PlayerChangedListener> playerListeners = new ArrayList<>();

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
        currentPlayerNumber++;
        if (currentPlayerNumber == players.size()) {
            currentPlayerNumber = 0;
            turnChanged();
        }
        playerChanged();
    }

    public static void subscribeTurnChanged(TurnListener listener) {
        turnListeners.add(listener);
    }

    public static void turnChanged() {
        if (turnNumber < Game.getInstance().totalTurns)
            turnNumber++;
        List<Goal> goalsToRemove = new ArrayList<>();
        for (Player player : players) {
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

    public static int getTurnNumber() {
        return turnNumber;
    }
}