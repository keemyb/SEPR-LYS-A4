package gamelogic.replay;

import fvs.taxe.controller.*;
import gamelogic.game.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class EventReplayer {
    public static final float MIN_PLAYBACK_SPEED = 1;
    public static final float MAX_PLAYBACK_SPEED = 3;

    Context context;
    static List<ReplayEvent> eventInstances = new ArrayList<>();
    ReplayEvent eventInstanceLastPlayed;
    ReplayEvent eventInstanceToPlayNext;
    com.badlogic.gdx.utils.Timer timer = new com.badlogic.gdx.utils.Timer();

    ConnectionController connectionController;
    GoalController goalController;
    RouteController routeController;
    TrainController trainController;

    static boolean isReplaying = false;

    private static List<ReplayListener> replayListeners = new ArrayList<>();
    private static List<ReplayModeListener> replayModeListeners = new ArrayList<>();
    private static float playBackSpeed = 1;

    public EventReplayer(Context context) {
        this.context = context;
        connectionController = context.getConnectionController();
        goalController = context.getGoalController();
        routeController = context.getRouteController();
        trainController = context.getTrainController();
    }

    public static void setPlayBackSpeed(float playBackSpeed) {
        EventReplayer.playBackSpeed = playBackSpeed;
    }

    public static void saveReplayEvent(ReplayEvent event) {
        if (isReplaying) return;
        System.out.println(event.gameEvent + " " + event.object);
        eventInstances.add(event);
    }

    public void playBackReplayEvent(ReplayEvent event) {
        System.out.println(event.gameEvent + " " + event.object);
        fireReplayEvent(event.gameEvent, event.object);
    }

    public void start() {
        System.out.println("begin");
        if (eventInstances.isEmpty()) return;
        eventInstanceToPlayNext = eventInstances.get(0);

        setReplaying(true);

        context.resetGame();
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
            }, eventInstanceToPlayNext.gameEvent.delay / playBackSpeed);
        } else {
            eventInstanceToPlayNext = null;
        }
    }

    public void pause() {
        timer.clear();
        timer.stop();
    }

    public void stop() {
        pause();
        setReplaying(false);
        eventInstances.clear();
    }

    public boolean anyMoreEventsToPlay() {
        return eventInstanceToPlayNext != null;
    }

    public static void subscribeReplayEvent(ReplayListener listener) {
        replayListeners.add(listener);
    }

    public void fireReplayEvent(GameEvent gameEvent, Object object) {
        for (ReplayListener listener : new ArrayList<>(replayListeners)) {
            listener.replay(gameEvent, object);
        }
    }

    public static void subscribeReplayModeEvent(ReplayModeListener listener) {
        replayModeListeners.add(listener);
    }

    public static void replayModeChanged() {
        for (ReplayModeListener listener : new ArrayList<>(replayModeListeners)) {
            listener.changed(isReplaying);
        }
    }

    public static boolean isReplaying() {
        return isReplaying;
    }

    public static void setReplaying(boolean willBeReplaying) {
        isReplaying = willBeReplaying;
        replayModeChanged();
    }
}
