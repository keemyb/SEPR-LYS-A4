package gamelogic.player;

import gamelogic.game.Game;
import gamelogic.goal.Goal;
import gamelogic.goal.GoalManager;
import gamelogic.map.Connection;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents player in the game. Players complete goals by using trains (trains and power-ups). By
 * completing goals players get score.
 */
public class Player {
    public static final int INITIAL_AMOUNT_OF_MONEY = 500;
    public static final int MONEY_PER_TURN = 100;

    private List<Train> trains = new ArrayList<>();
    private Set<Connection> connectionsOwned = new HashSet<>();
    private List<Goal> goals = new ArrayList<>();
    private int playerNumber;
    private int money = INITIAL_AMOUNT_OF_MONEY;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public void reset() {
        trains.clear();
        connectionsOwned.clear();
        goals.clear();

        money = INITIAL_AMOUNT_OF_MONEY;
    }

    public List<Train> getTrains() {
        return trains;
    }

    public void addTrain(Train train) {
        trains.add(train);
        changed();
    }

    public void removeTrain(Train train) {
        trains.remove(train);
        train.dispose();
        changed();
    }

    public void addOwnedConnection(Connection connection) {
        connectionsOwned.add(connection);
        connection.setOwner(this);
        changed();
    }

    public void removeOwnedConnection(Connection connection) {
        connectionsOwned.remove(connection);
        connection.setOwner(null);
        changed();
    }

    public void addGoal(Goal goal) {
        int incompleteGoals = 0;
        for (Goal existingGoal : goals)
            incompleteGoals += (existingGoal.getComplete()) ? 0 : 1;
        if (incompleteGoals >= GoalManager.CONFIG_MAX_PLAYER_GOALS)
            return;
        goals.add(goal);
        changed();
    }

    public void discardGoal(Goal goal) {
        goals.remove(goal);
        changed();
    }

    public void completeGoal(Goal goal) {
        goal.setComplete();
        money += goal.getMoney();
        changed();
    }

    public void payConnectionRent(Connection connection) {
        Player connectionOwner = connection.getOwner();
        // If I own the connection or nobody does, there is no-one to pay
        if (connectionOwner == null || connectionOwner == this) return;
        int amount = connection.getRentPayable();
        System.out.println("Player " + playerNumber + " paid Player " + connectionOwner.playerNumber + " " + Game.CURRENCY_SYMBOL + amount +
        " for the use of the Connection from " + connection.getStation1().getName() + " to " + connection.getStation2().getName() + ".");
        connectionOwner.money += amount;
        money -= amount;
        changed();
    }

    public void spendMoney(int amount) {
        money -= amount;
        changed();
    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's trains changes
     */
    public void changed() {
        PlayerManager.playerChanged();
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getMoney() {
        return money;
    }

    public Set<Connection> getConnectionsOwned() {
        return connectionsOwned;
    }
}
