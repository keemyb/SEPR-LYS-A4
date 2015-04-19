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
    private int reward;
    private Station origin;
    private Station destination;
    private int turnIssued;
    private boolean complete = false;
    private Train requiredTrain = null;            // Train constraint
    private boolean quantifiable = false;       // Quantifiable or not
    private int turnLimit = 0;                  // If quantifiable: turn limits constraint

    public Goal(Station origin, Station destination, int turnIssued) {
        this.origin = origin;
        this.destination = destination;
        this.turnIssued = turnIssued;
        updateGoal();
    }

    public void addTrainConstraint(Train train) {
        requiredTrain = train;
        updateGoal();
    }

    public void addTurnLimitConstraint(int turns) {
        quantifiable = true;
        turnLimit = turns;
        updateGoal();
    }

    public void resetComplete() {
        complete = false;
    }

    public boolean isComplete(Train train) {
        if (!train.historyContains(origin, turnIssued)) return false;
        if (!train.historyContains(destination, turnIssued)) return false;

        int turnOriginWasVisited = train.getLastTurnStationWasVisited(origin);
        int turnDestinationWasVisited = train.getLastTurnStationWasVisited(destination);
        if (turnOriginWasVisited > turnDestinationWasVisited) return false;

        if (requiredTrain != null) {
            if (!train.equals(requiredTrain)) return false;
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
        int distance = (int) Game.getInstance().getMap().getShortestRouteDistance(origin, destination);
        if (quantifiable) {
            if (turnIssued != PlayerManager.getTurnNumber()) {
                turnLimit--;
            }
            float t = distance * 5 * (1 - 1f / turnLimit);
            reward = Math.max(50, (int) t - (int) t % 50);
        } else {
            reward = Math.max(50, distance - distance % 50);
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

}