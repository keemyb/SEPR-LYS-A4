package gamelogic.resource;

import gamelogic.player.Player;
import util.Disposable;

/**
 * This class provides a skeletal implementation of player resources in the game. Additional types of resources should
 * be added by extending this class.
 */
public abstract class Resource implements Disposable {
    protected String name;
    private Player player;

    public boolean isOwnedBy(Player player) {
        return player == this.player;
    }

    @Override
    public String toString() {
        return name;
    }

    protected void changed() {
        player.changed();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }
}