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
            // always true, really?
        } while (destination == origin || destination instanceof Junction);

        Goal goal = new Goal(origin, destination, turn);

        // Goal with a specific train
        Random random = new Random();
        if (random.nextInt(2) == 1) {
            goal.addConstraint("train", trainManager.getTrainNames().get(random.nextInt(trainManager.getTrainNames().size())));
        }

        return goal;
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
        System.out.println("Train arrived to final destination: " + train.getFinalDestination().getName());
        return completedString;
    }
}
