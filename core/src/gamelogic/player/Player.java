package gamelogic.player;

import gamelogic.goal.Goal;
import gamelogic.goal.GoalManager;
import gamelogic.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents player in the game. Players complete goals by using resources (trains and power-ups). By
 * completing goals players get score.
 */
public class Player {
    public static final int INITIAL_AMOUNT_OF_MONEY = 500;

    private List<Resource> resources = new ArrayList<>();
    private List<Goal> goals = new ArrayList<>();
    private int playerNumber;
    private int score = 0;
    private int money = INITIAL_AMOUNT_OF_MONEY;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        changed();
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
        resource.dispose();
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

    public void completeGoal(Goal goal) {
        goal.setComplete();
        score += goal.getScore();
        money += goal.getMoney();
        changed();
    }

    public void payPlayer(Player player, int amount) {
        player.money += amount;
        money -= amount;
    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's resources changes
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

    public int getScore() {
        return score;
    }

    public int getMoney() {
        return money;
    }

}
