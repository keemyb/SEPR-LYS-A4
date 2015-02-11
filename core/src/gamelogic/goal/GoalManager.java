package gamelogic.goal;

import gamelogic.Game;
import gamelogic.Player;
import gamelogic.map.Junction;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;

import java.util.ArrayList;
import java.util.Random;

public class GoalManager {
    public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    private TrainManager trainManager;

    public GoalManager(TrainManager trainManager) {
        this.trainManager = trainManager;
    }

    private Goal generateRandom(int turn) {
        Map map = Game.getInstance().getMap();

        Station origin;
        do {
            origin = map.getRandomStation();
        } while (origin instanceof Junction);

        Station destination;
        do {
            destination = map.getRandomStation();
            // always true, really? Can confirm, is bollocks. Damn it intelliJ!
        } while (destination == origin || destination instanceof Junction);

        Goal goal = new Goal(origin, destination, turn);

        // Goal with a specific train
        Random random = new Random();
        double randDouble = random.nextDouble();

        if (random.nextInt(3) == 1) {
            int phase = (int) Math.floor((turn / Game.getInstance().totalTurns) * 3.0);
            if (phase == 0) {
                if (randDouble < 0.9) {
                    goal.addConstraint("train", trainManager.getTrainNames().get(4));
                } else {
                    goal.addConstraint("train", trainManager.getTrainNames().get(3));
                }
            } else if (phase == 1) {
                if (randDouble < 0.15) {
                    goal.addConstraint("train", trainManager.getTrainNames().get(4));
                } else if (randDouble < 0.7) {
                    goal.addConstraint("train", trainManager.getTrainNames().get(3));
                } else {
                    goal.addConstraint("train", trainManager.getTrainNames().get(2));
                }
            } else {
                if (randDouble < 0.6) {
                    goal.addConstraint("train", trainManager.getTrainNames().get(1));
                } else {
                    goal.addConstraint("train", trainManager.getTrainNames().get(0));
                }
            }
            //Make goal quantifiable
            if (random.nextInt(3) >= 0){
                //add a turn limit to the goal
                goal.addTurnLimit(computeTurnLimit(goal.getOrigin(), goal.getDestination(), goal.getTrainName()));
            }
        }

        return goal;
    }

    private int computeTurnLimit(Station origin, Station destination, String trainName){
        int distance = (int)Game.getInstance().getMap().getDistance(origin, destination);
        return distance / trainManager.getSpeedOfTRain(trainName) + 5;
    }

    public void addRandomGoalToPlayer(Player player) {
        player.addGoal(generateRandom(player.getPlayerManager().getTurnNumber()));
    }

    public ArrayList<String> trainArrived(Train train, Player player) {
        ArrayList<String> completedString = new ArrayList<String>();
        for (Goal goal : player.getGoals()) {
            if (goal.isComplete(train)) {
                player.completeGoal(goal);
                player.removeResource(train);
                completedString.add("Player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
            }
        }
        System.out.println("Train arrived to final destination: " +
                Game.getInstance().getMap().getStationFromPosition(train.getFinalDestination()).getName());
        return completedString;
    }
}
