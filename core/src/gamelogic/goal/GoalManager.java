package gamelogic.goal;

import gamelogic.game.Game;
import gamelogic.map.Junction;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;
import gamelogic.resource.TrainManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Abstract class providing functions for manipulating goals.
 */
public abstract class GoalManager {

    public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    private final static Random random = new Random();

    /**
     * Returns a random goal. The type of goal is dependent on phase of the game.
     *
     * @param turn current turn
     * @return random goal
     */
    private static Goal generateRandom(int turn) {
        Map map = Game.getInstance().getMap();

        Station origin, destination;
        do {
            origin = map.getRandomStation();
        } while (origin instanceof Junction);
        do {
            destination = map.getRandomStation();
        } while (destination == origin || destination instanceof Junction);
        Goal goal = new Goal(origin, destination, turn);

        // Goal with a specific train; pretty much hardcoded configuration
        double randDouble = random.nextDouble();
        if (random.nextInt(3) == 1) {
            int phase = Game.getInstance().getPhase();
            if (phase == 0) {
                if (randDouble < 0.9) {
                    goal.addTrainConstraint(TrainManager.trains.get(4));
                } else {
                    goal.addTrainConstraint(TrainManager.trains.get(3));
                }
            } else if (phase == 1) {
                if (randDouble < 0.15) {
                    goal.addTrainConstraint(TrainManager.trains.get(4));
                } else if (randDouble < 0.7) {
                    goal.addTrainConstraint(TrainManager.trains.get(3));
                } else {
                    goal.addTrainConstraint(TrainManager.trains.get(2));
                }
            } else {
                if (randDouble < 0.6) {
                    goal.addTrainConstraint(TrainManager.trains.get(1));
                } else {
                    goal.addTrainConstraint(TrainManager.trains.get(0));
                }
            }
        }

        //Make goal quantifiable
        if (random.nextInt(3) == 1) {
            goal.addTurnLimitConstraint(computeTurnLimit(goal.getOrigin(), goal.getDestination()));
        }
        return goal;
    }

    private static int computeTurnLimit(Station origin, Station destination) {
        int distance = (int) Game.getInstance().getMap().getShortestRouteDistance(origin, destination);
        return distance / TrainManager.getRandomTrain().getSpeed() + 5;
    }

    public static void addRandomGoalToPlayer(Player player) {
        player.addGoal(generateRandom(PlayerManager.getTurnNumber()));
    }

    /**
     * Executed when a train has arrived to its destination.
     *
     * @param train  train
     * @param player player
     * @return list of strings saying which goals were completed.
     */
    public static ArrayList<String> trainArrived(Train train, Player player) {
        ArrayList<String> completedStrings = new ArrayList<>();
        for (Goal goal : player.getGoals()) {
            if (goal.isComplete(train)) {
                player.completeGoal(goal);
                player.removeResource(train);
                completedStrings.add("Player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
            }
        }
        System.out.println("Train arrived to final destination: " + train.getFinalStation().getName());
        return completedStrings;
    }
}
