package gamelogic.replay;

import fvs.taxe.controller.*;
import gamelogic.game.GameEvent;
import gamelogic.map.Station;
import gamelogic.resource.Train;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EventReplayer {
    Context context;
    List<ReplayEvent> eventInstances = new ArrayList<>();
    ReplayEvent eventInstanceLastPlayed;
    ReplayEvent eventInstanceToPlayNext;
    Timer timer;

    ConnectionController connectionController;
    GoalController goalController;
    RouteController routeController;
    TrainController trainController;

    private static List<ReplayListener> replayListeners = new ArrayList<>();

    public EventReplayer(Context context) {
        this.context = context;
        timer = new Timer(0, null);

        connectionController = context.getConnectionController();
        goalController = context.getGoalController();
        routeController = context.getRouteController();
        trainController = context.getTrainController();
    }

    public void reset() {
        eventInstances.clear();
    }

    public void saveReplayEvent(ReplayEvent event) {
        eventInstances.add(event);
    }

    public void playBackReplayEvent(ReplayEvent event) {
        switch (event.gameEvent) {
            case CLICKED_TRAIN:
                break;
            case CLICKED_PLACE_TRAIN:
                break;
            case CLICKED_PLACE_TRAIN_DISCARD:
                break;
            case CLICKED_STATION:
                break;
            case CLICKED_CHOOSE_ROUTE:
                break;
            case CLICKED_CHOOSE_ROUTE_DISCARD:
                break;
            case CLICKED_ROUTE_COMPLETE:
                break;
            case CLICKED_ROUTE_COMPLETE_CANCEL:
                break;
            case CLICKED_END_TURN:
                break;
            case CLICKED_GOAL_BUTTON:
                break;
            case CLICKED_DISCARD_GOAL:
                break;
            case CLICKED_DISCARD_GOAL_CANCEL:
                break;
            case CLICKED_CONNECTION_BUTTON:
                break;
            case CLICKED_ADD_CONNECTION:
                break;
            case CLICKED_CHOOSE_NEW_CONNECTION_MATERIAL:
                break;
            case CLICKED_CHOOSE_NEW_CONNECTION_MATERIAL_CANCEL:
                break;
            case CLICKED_EDIT_CONNECTION:
                break;
            case CLICKED_REPAIR_CONNECTION:
                break;
            case CLICKED_CHOOSE_REPAIR_CONNECTION_AMOUNT:
                break;
            case CLICKED_CHOOSE_REPAIR_CONNECTION_AMOUNT_CANCEL:
                break;
            case CLICKED_UPGRADE_CONNECTION:
                break;
            case CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL:
                break;
            case CLICKED_CHOOSE_UPGRADE_CONNECTION_MATERIAL_CANCEL:
                break;
            case CLICKED_REMOVE_CONNECTION:
                break;
            case CLICKED_REMOVE_CONNECTION_CANCEL:
                break;
        }
    }

    public void start() {
        if (eventInstances.isEmpty()) return;
        eventInstanceToPlayNext = eventInstances.get(0);

        play();
    }

    private void play() {
        if (eventInstanceToPlayNext == null) return;

        playBackReplayEvent(eventInstanceToPlayNext);
        eventInstanceLastPlayed = eventInstanceToPlayNext;
        eventInstanceToPlayNext = eventInstances.get(eventInstances.indexOf(eventInstanceLastPlayed) + 1);
    }

    public static void subscribeReplayEvent(ReplayListener listener) {
        replayListeners.add(listener);
    }

    public static void fireReplayEvent(GameEvent gameEvent, Object object) {
        for (ReplayListener listener : replayListeners) {
            listener.replay(gameEvent, object);
        }
    }
}
