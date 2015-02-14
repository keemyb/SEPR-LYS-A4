package gamelogic.goal;

import gamelogic.Game;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import util.Tuple;

/**
 * This class represents goals in the game. Goals can have two constraints: train constraint (a goal must be completed by a specific train)
 * and a turn limit constraint (goal must be completed within specific number of turns - it is quantifiable).
 */
public class Goal {
    private int score;
    private Station origin;
    private Station destination;
    private int turnIssued;
    private boolean complete = false;
    private String trainName = null;            // Train constraint
    private boolean quantifiable = false;       // Quantifiable or not
    private int turnLimit = 0;                  // If quantifiable: turn limits constraint

    public Goal(Station origin, Station destination, int turnIssued) {
        this.origin = origin;
        this.destination = destination;
        this.turnIssued = turnIssued;
        updateGoal();
    }

    public void addTrainConstraint(String trainName) {
        this.trainName = trainName;
        updateGoal();
    }

    public void addTurnLimitConstraint(int turns){
        quantifiable = true;
        turnLimit = turns;
        updateGoal();
    }

    public boolean isComplete(Train train) {
        boolean passedOrigin = false;
        for (Tuple<String, Integer> history : train.getHistory())
            if (history.getFirst().equals(origin.getName()) && history.getSecond() >= turnIssued)
                passedOrigin = true;
        return train.getFinalStation() == destination && passedOrigin &&
                (trainName == null || trainName.equals(train.getName()));
    }

    public String toString() {
        String trainString = (trainName == null) ? "train" : trainName;
        String turnLimit = Integer.toString(getTurnLimit());
        return "Send a " + trainString +
                " from " + origin.getName() + "(" + origin.getAbbreviation() + ")" +
                " to " + destination.getName() + "(" + destination.getAbbreviation() + ")" +
                ((quantifiable) ? " in " + turnLimit + " turns" : "") +
                ": " + score;
    }

    public void setComplete() {
        complete = true;
    }

    public boolean getComplete() {
        return complete;
    }

    public void updateGoal() {
        int distance = (int)Game.getInstance().getMap().getShortestRouteDistance(origin, destination);
        if (quantifiable) {
            if (turnIssued != Game.getInstance().getPlayerManager().getTurnNumber()) {
                turnLimit--;
            }
            float t = distance * 5 * (1 - 1f / turnLimit);
            score = Math.max(50, (int)t - (int)t % 50);
        }
        else {
            score = Math.max(50, distance - distance % 50);
        }
    }

    // Only quantifiable goals can expire.
    public boolean isExpired() {
        return (quantifiable && turnLimit == 0);
    }

    public int getScore() {
        return score;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getDestination() {
        return destination;
    }

    public int getTurnLimit(){
        return turnLimit;
    }

}