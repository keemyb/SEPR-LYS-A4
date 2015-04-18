package gamelogic.replay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import fvs.taxe.controller.*;
import gamelogic.game.GameEvent;
import gamelogic.map.Station;
import gamelogic.resource.Train;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class EventReplayer {
    Context context;
    List<ReplayEvent> eventInstances = new ArrayList<>();
    ReplayEvent eventInstanceLastPlayed;
    ReplayEvent eventInstanceToPlayNext;
    com.badlogic.gdx.utils.Timer timer = new com.badlogic.gdx.utils.Timer();

    ConnectionController connectionController;
    GoalController goalController;
    RouteController routeController;
    TrainController trainController;

    private static List<ReplayListener> replayListeners = new ArrayList<>();

    public EventReplayer(Context context) {
        this.context = context;
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
    }

    public void play() {
        if (!anyMoreEventsToPlay()) return;

        playBackReplayEvent(eventInstanceToPlayNext);
        eventInstanceLastPlayed = eventInstanceToPlayNext;

        int nextEventIndex = eventInstances.indexOf(eventInstanceLastPlayed) + 1;
        if (nextEventIndex < eventInstances.size()) {
            eventInstanceToPlayNext = eventInstances.get(nextEventIndex);
            timer.scheduleTask(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    play();
                }
            }, 5);
        } else {
            eventInstanceToPlayNext = null;
        }
    }

    public void pause() {
        timer.clear();
        timer.stop();
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
