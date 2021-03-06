package gamelogic.goal;

import gamelogic.game.Game;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;

/**
 * This class represents goals in the game. Goals can have two constraints: train constraint (a goal must be completed
 * by a specific train) and a turn limit constraint (goal must be completed within specific number of turns - it is
 * quantifiable).
 */
public class Goal {
    // The bonus granted when a goal crosses a zone, and there is no existing path
    // between the two zones.
    private static final int INTER_ZONE_GOAL_BONUS = 500;
    private static final int BASE_REWARD = 100;
    private static final float REWARD_DEPRECIATION = 0.15f;

    private int reward;
    private Station origin;
    private Station destination;
    private int turnIssued;
    private boolean complete = false;
    private Train requiredTrain = null;
    private boolean quantifiable = false;
    private int originalTurnLimit = 0;
    private int turnLimit = 0;                  // If quantifiable
    // If there was no path at the time a goal was created, we give a bonus
    private boolean pathBetweenStationsExist = true;

    public Goal(Station origin, Station destination, int turnIssued) {
        this.origin = origin;
        this.destination = destination;
        this.turnIssued = turnIssued;
        setReward();
        updateGoal();
    }

    public void addTrainConstraint(Train train) {
        requiredTrain = train;
        setReward();
    }

    public void addTurnLimitConstraint(int turnLimit) {
        quantifiable = true;
        originalTurnLimit = turnLimit;
        this.turnLimit = turnLimit;
        setReward();
    }

    public void reset() {
        complete = false;
        turnLimit = originalTurnLimit;
        setReward();
    }

    public boolean isComplete(Train train) {
        if (!train.historyContains(origin, turnIssued)) return false;
        if (!train.historyContains(destination, turnIssued)) return false;

        int turnOriginWasVisited = train.getLastTurnStationWasVisited(origin);
        int turnDestinationWasVisited = train.getLastTurnStationWasVisited(destination);
        if (turnOriginWasVisited > turnDestinationWasVisited) return false;

        if (requiredTrain != null) {
            if (!train.getName().equals(requiredTrain.getName())) return false;
        }

        return true;
    }

    public String toString() {
        String trainString = (requiredTrain == null) ? "train" : requiredTrain.getName();
        String turnLimit = Integer.toString(getTurnLimit());
        return "Send a " + trainString +
                " from " + origin.getName() +
                " to " + destination.getName() +
                ((quantifiable) ? " in " + turnLimit + " turns" : "") +
                " to earn " + Game.CURRENCY_SYMBOL + reward;
    }

    public void setComplete() {
        complete = true;
    }

    public boolean getComplete() {
        return complete;
    }

    public void updateGoal() {
        if (quantifiable) {
            if (turnIssued != PlayerManager.getTurnNumber()) {
                turnLimit--;
                reward -= Math.round((float) reward / 100f) * REWARD_DEPRECIATION * 100;
            }
        }
    }

    private void setReward(){
        reward = BASE_REWARD;
        int distance = (int) Game.getInstance().getMap().getLengthOfShortestRoute(origin, destination);

        if (quantifiable){
            float t = distance * 3 * (1 - 1f / turnLimit);
            reward += Math.max(50, (int) t - (int) t % 50);
        } else {
            reward += Math.max(50, distance - distance % 50);
        }

        if (!pathBetweenStationsExist){
            reward += INTER_ZONE_GOAL_BONUS;
        }
    }

    // Only quantifiable goals can expire.
    public boolean isExpired() {
        return (quantifiable && turnLimit == 0);
    }

    public int getMoney() {
        return reward;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getDestination() {
        return destination;
    }

    public int getTurnLimit() {
        return turnLimit;
    }

    public void setPathBetweenStationsExist(boolean pathBetweenStationsExist) {
        this.pathBetweenStationsExist = pathBetweenStationsExist;
        setReward();
    }

}