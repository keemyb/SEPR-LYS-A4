package gamelogic.goal;

import gamelogic.Game;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import util.Tuple;

public class Goal {
    private int score;
    private Station origin;
    private Station destination;
    private int turnIssued;
    private boolean complete = false;
    //constraints
    private String trainName = null;
    //limits
    private boolean quantifiable = false;
    private int turnLimit = 0;

    public Goal(Station origin, Station destination, int turn) {
        this.origin = origin;
        this.destination = destination;
        this.turnIssued = turn;
        updateGoal();
    }

    public void addConstraint(String name, String value) {
        if (name.equals("train")) {
            trainName = value;
        } else {
            throw new RuntimeException(name + " is not a valid goal constraint");
        }
        updateGoal();
    }

    public void addTurnLimit(int turns){
        quantifiable = true;
        turnLimit = turns;
        updateGoal();
    }

    public boolean isComplete(Train train) {
        boolean passedOrigin = false;
        for (Tuple<String, Integer> history : train.getHistory()) {
            if (history.getFirst().equals(origin.getName()) && history.getSecond() >= turnIssued) {
                passedOrigin = true;
            }
        }
        if (train.getFinalStation() == destination && passedOrigin) {
            if (trainName == null || trainName.equals(train.getName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        String trainString = "train";
        String turnLimit = Integer.toString(getTurnLimit());
        String goalString;
        if (trainName != null && !quantifiable) {
            trainString = trainName;
            goalString = "Send a " + trainString + " from " + origin.getName() + "(" + origin.getAbbreviation() + ")"+ " to " + destination.getName() + "(" + destination.getAbbreviation() +")" + " : " + score ;
        }
        else if (trainName != null && quantifiable) {
            trainString = trainName;
            goalString = "Send a " + trainString + " from " + origin.getName() + "(" + origin.getAbbreviation() + ")"+ " to " + destination.getName() + "(" + destination.getAbbreviation() +")" + " in " + turnLimit + " turns : " + score ;
        }
        else{
            goalString = "Send a " + trainString + " from " + origin.getName() + "(" + origin.getAbbreviation() + ")"+ " to " + destination.getName() + "(" + destination.getAbbreviation() +")" + " : " + score ;
        }

        return goalString;
    }

    public void setComplete() {
        complete = true;
    }

    public boolean getComplete() {
        return complete;
    }

    public void updateGoal() {
        int distance = (int)Game.getInstance().getMap().getDistance(origin, destination);
        if (quantifiable) {
            if (turnIssued != Game.getInstance().getPlayerManager().getTurnNumber()) {
                turnLimit--;
            }

            float t = distance * 5f * (1f - 1f / turnLimit);
            score = Math.max(50, (int)t - (int)t % 50);
        }
        else {
            score = Math.max(50, distance - distance % 50);
        }
    }

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

    public String getTrainName() {
        return trainName;
    }

    public int getTurnLimit(){
        return turnLimit;
    }

    public int getTurnIssued() {
        return turnIssued;
    }

}