package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actor.TrainActor;
import gamelogic.game.Game;
import gamelogic.map.*;
import gamelogic.player.Player;
import gamelogic.goal.GoalManager;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public class TrainMoveController {
    private Context context;
    private Map map;
    private Train train;

    public TrainMoveController(Context context, Train train) {
        this.context = context;
        map = context.getGameLogic().getMap();
        this.train = train;
    }

    /**
     * An action for the train to run before it starts moving across the screen
     * @return
     */
    private RunnableAction beforeAction() {
        return new RunnableAction() {
            public void run() {
                train.getActor().setVisible(true);
                train.setPosition(new Position(-1, -1));
                train.setAtStation(false);
                train.setLocation(null);
            }
        };
    }

    /**
     * This action will run every time the train reaches a station within a route
     * @param station the station that was reached
     * @return
     */
    private RunnableAction perStationAction(final Station station) {
        return new RunnableAction() {
            public void run() {
                train.addToHistory(station, PlayerManager.getTurnNumber());
                if (train.getHistory().size() >= 2) {
                    Station secondLastStation = train.getHistory().get(train.getHistory().size() - 2).getFirst();
                    // If a train backtracks then the connection will be null.
                    if (secondLastStation != station) {
                        Connection visited = map.getConnectionBetween(secondLastStation, station);
                        ConnectionController.visitedConnection(train, visited);
                    }
                }
                checkCollisions(station);
                train.getRoute().remove(station);
            }
        };
    }

    private Action waitUntilPassableAction (final Junction junction) {
        return new Action() {
            @Override
            public boolean act(float delta) {
                return junction.isPassable();
            }
        };
    }

    /**
     * An action for the train to run after it has moved the whole route
     * @return
     */
    private RunnableAction afterAction() {
        return new RunnableAction() {
            public void run() {
                ArrayList<String> completedGoals = GoalManager.trainArrived(train, train.getPlayer());
                for (String message : completedGoals) {
                    context.getTopBarController().displayFlashMessage(message, Color.BLUE, 2);
                }
                train.setPosition(train.getFinalStation().getLocation());
                train.getActor().setVisible(false);
                train.arrivedAtDestination();
            }
        };
    }

    public void addMoveActionsForRoute() {
        SequenceAction action = Actions.sequence();
        Position current = train.getPosition();
        action.addAction(beforeAction());

        for (final Station next : train.getRoute()) {
            Position nextPosition = next.getLocation();
            Connection connection = map.getConnectionBetween(current, nextPosition);
            float duration = Position.getDistance(current, nextPosition) / connection.calculateAdjustedTrainSpeed(train);
            action.addAction(moveTo(nextPosition.getX() - TrainActor.width / 2, nextPosition.getY() - TrainActor.height / 2, duration));
            Station station = Game.getInstance().getMap().getStationByPosition(nextPosition);
            if (station != null) {
                action.addAction(perStationAction(station));
                if (station instanceof Junction)
                    action.addAction(waitUntilPassableAction((Junction)station));
            }
            current = nextPosition;
        }
        action.addAction(afterAction());

        // remove previous actions to be cautious
        train.getActor().clearActions();
        train.getActor().addAction(action);
    }

    private void checkCollisions(Station station) {
        //test for train collisions at Junction point
        if (!(station instanceof Junction)) {
            return;
        }

        List<Train> trainsToDestroy = trainsToDestroy();
        if (trainsToDestroy.size() > 0) {
            for (Train trainToDestroy : trainsToDestroy) {
                trainToDestroy.getActor().remove();
                trainToDestroy.getPlayer().removeTrain(trainToDestroy);
            }

            context.getTopBarController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.RED, 2);
        }
    }

    private List<Train> trainsToDestroy() {
        List<Train> trainsToDestroy = new ArrayList<>();

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Train otherTrain : player.getTrains()) {
                if (otherTrain == train) continue;
                if (otherTrain.getActor() == null) continue;

                if (train.getActor().getBounds().overlaps(otherTrain.getActor().getBounds())) {
                    trainsToDestroy.add(train);
                    trainsToDestroy.add(otherTrain);
                }
            }
        }

        return trainsToDestroy;
    }
}
