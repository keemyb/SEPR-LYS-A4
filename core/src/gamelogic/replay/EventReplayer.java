package gamelogic.replay;

import fvs.taxe.controller.*;
import gamelogic.game.GameEvent;
import gamelogic.map.Connection;
import gamelogic.map.Junction;
import gamelogic.map.Map;
import gamelogic.map.Station;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Train;

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
        System.out.println(event.gameEvent);
        eventInstances.add(event);
    }

    public void playBackReplayEvent(ReplayEvent event) {
        fireReplayEvent(event.gameEvent, event.object);
    }

    public void start() {
        System.out.println("---start---");
        if (eventInstances.isEmpty()) return;
        eventInstanceToPlayNext = eventInstances.get(0);

        setReplaying(true);

        PlayerManager.reset();
        clearTrainActors();
        resetMapAttributes();
        resetPlayerAttributes();
    }

    private void clearTrainActors() {
        for (Player player : PlayerManager.getAllPlayers()) {
            for (Train train : player.getTrains()) {
                train.reset();
            }
        }
    }

    private void resetMapAttributes() {
        Map map = context.getGameLogic().getMap();

        for (Connection connection : new ArrayList<>(map.getConnections())) {
            if (connection.getOwner() != null) {
                map.removeConnection(connection);
            }
        }

        for (Station station : map.getStations()) {
            if (!(station instanceof Junction)) continue;

            Junction junction = (Junction) station;
            junction.reset();
        }
    }

    private void resetPlayerAttributes() {
        for (Player player : PlayerManager.getAllPlayers()) {
            player.reset();
        }
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

    public boolean anyMoreEventsToPlay() {
        return eventInstanceToPlayNext != null;
    }

    public static void subscribeReplayEvent(ReplayListener listener) {
        replayListeners.add(listener);
    }

    public void fireReplayEvent(GameEvent gameEvent, Object object) {
        System.out.println(gameEvent);
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
