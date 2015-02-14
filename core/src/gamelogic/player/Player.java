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
    private List<Resource> resources;
    private List<Goal> goals;
    private int playerNumber;
    private int score = 0;

    public Player(int playerNumber) {
        goals = new ArrayList<>();
        resources = new ArrayList<>();
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
        changed();
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
}
