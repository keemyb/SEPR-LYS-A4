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

    public Goal(Station origin, Station destination, int turn) {
        this.origin = origin;
        this.destination = destination;
        this.turnIssued = turn;
        updateScore();
    }

    public void addConstraint(String name, String value) {
        if (name.equals("train")) {
            trainName = value;
        } else {
            throw new RuntimeException(name + " is not a valid goal constraint");
        }
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
        if (trainName != null) {
            trainString = trainName;
        }
        return "Send a " + trainString + " from " + origin.getName() + "(" + origin.getAbbreviation() + ")"+ " to " + destination.getName() + "(" + destination.getAbbreviation() +")" + " : " + score ;
    }

    public void setComplete() {
        complete = true;
    }

    public boolean getComplete() {
        return complete;
    }

    public void updateScore() {
        int distance = (int)Game.getInstance().getMap().getDistance(origin, destination);
        score = Math.max(50, distance - distance % 50);
    }

    public int getScore() {
        return score;
    }

}