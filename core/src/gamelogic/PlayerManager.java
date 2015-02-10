package gamelogic;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private ArrayList<Player> players = new ArrayList<Player>();
    private int currentTurn = 0;
    private int turnNumber = 0;
    private List<TurnListener> turnListeners = new ArrayList<TurnListener>();
    private List<PlayerChangedListener> playerListeners = new ArrayList<PlayerChangedListener>();

    public void createPlayers(int count) {
        for (int i = 0; i < count; i++) {
            players.add(new Player(this, i + 1));
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    public void turnOver() {
        if (currentTurn == 0) {
            currentTurn = 1;
        } else {
            currentTurn = 0;
            turnChanged();
        }
        playerChanged();
    }

    public void subscribeTurnChanged(TurnListener listener) {
        turnListeners.add(listener);
    }

    private void turnChanged() {
        if (turnNumber < Game.getInstance().TOTAL_TURNS) turnNumber++;
        for (TurnListener listener : turnListeners) {
            listener.changed();
        }
    }

    public void subscribePlayerChanged(PlayerChangedListener listener) {
        playerListeners.add(listener);
    }

    // very general event which is fired when player's goals / resources are changed
    public void playerChanged() {
        for (PlayerChangedListener listener : playerListeners) {
            listener.changed();
        }
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}
