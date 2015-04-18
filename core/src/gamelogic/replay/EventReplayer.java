package gamelogic.replay;

import com.badlogic.gdx.utils.Array;
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
        if (context.isReplaying()) return;
        eventInstances.add(event);
    }

    public void playBackReplayEvent(ReplayEvent event) {
        fireReplayEvent(event.gameEvent, event.object);
    }

    public void start() {
        if (eventInstances.isEmpty()) return;
        eventInstanceToPlayNext = eventInstances.get(0);

//        play();
    }

    public void play() {
        if (!anyMoreEventsToPlay()) return;

        playBackReplayEvent(eventInstanceToPlayNext);
        eventInstanceLastPlayed = eventInstanceToPlayNext;

        int nextEventIndex = eventInstances.indexOf(eventInstanceLastPlayed) + 1;
        if (nextEventIndex < eventInstances.size()) {
            eventInstanceToPlayNext = eventInstances.get(nextEventIndex);
        } else {
            eventInstanceToPlayNext = null;
        }
    }

    public boolean anyMoreEventsToPlay() {
        return eventInstanceToPlayNext != null;
    }

    public void subscribeReplayEvent(ReplayListener listener) {
        replayListeners.add(listener);
    }

    public void fireReplayEvent(GameEvent gameEvent, Object object) {
        for (ReplayListener listener : new ArrayList<>(replayListeners)) {
            listener.replay(gameEvent, object);
        }
    }
}
