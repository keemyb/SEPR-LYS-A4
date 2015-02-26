package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actor.TrainActor;
import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.goal.GoalManager;
import gamelogic.map.Junction;
import gamelogic.map.Position;
import gamelogic.map.Station;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public class TrainMoveController {
    private Context context;
    private Train train;

    public TrainMoveController(Context context, Train train) {
        this.context = context;
        this.train = train;

        addMoveActions();
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
                train.addToHistory(station.getName(), PlayerManager.getTurnNumber());
                System.out.println("Added to history: passed " + station.getName() + " on turn "
                        + PlayerManager.getTurnNumber());
                collisions(station);
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
                    context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);
                }
                System.out.println(train.getFinalStation().getLocation().getX() + "," + train.getFinalStation().getLocation().getY());
                train.setPosition(train.getFinalStation().getLocation());
                train.getActor().setVisible(false);
                train.arrivedAtDestination();
            }
        };
    }

    public void addMoveActions() {
        SequenceAction action = Actions.sequence();
        Position current = train.getPosition();
        action.addAction(beforeAction());

        for (final Station next : train.getRoute()) {
            Position nextPosition = next.getLocation();
            float duration = getDistance(current, nextPosition) / train.getSpeed();
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

    private float getDistance(Position a, Position b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private void collisions(Station station) {
        //test for train collisions at Junction point
        if (!(station instanceof Junction)) {
            return;
        }

        List<Train> trainsToDestroy = trainsToDestroy();
        if (trainsToDestroy.size() > 0) {
            for (Train trainToDestroy : trainsToDestroy) {
                trainToDestroy.getActor().remove();
                trainToDestroy.getPlayer().removeResource(trainToDestroy);
            }

            context.getTopBarController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.RED, 2);
        }
    }

    private List<Train> trainsToDestroy() {
        List<Train> trainsToDestroy = new ArrayList<>();

        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getResources()) {
                if (resource instanceof Train) {
                    Train otherTrain = (Train) resource;
                    if (otherTrain.getActor() == null) continue;
                    if (otherTrain == train) continue;

                    if (train.getActor().getBounds().overlaps(otherTrain.getActor().getBounds())) {
                        //destroy trains that have crashed and burned
                        trainsToDestroy.add(train);
                        trainsToDestroy.add(otherTrain);
                    }

                }
            }
        }

        return trainsToDestroy;
    }
}
