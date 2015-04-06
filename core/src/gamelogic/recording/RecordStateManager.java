package gamelogic.recording;

import gamelogic.game.Game;
import gamelogic.game.GameState;
import gamelogic.game.GameStateListener;

import java.util.HashMap;
import java.util.Map;

public class RecordStateManager {
    private Map<Integer, RecordState> recordStateMap = new HashMap<>();

    private Integer lastStateCaptured = 0;

    private Long lastTimePushed = System.currentTimeMillis();

    private gamelogic.map.Map map;

    private GameStateListener gameStateListener = new GameStateListener() {
        @Override
        public void changed(GameState state) {
            if (GameState.ANIMATING == state) {
                turnAnimationsStarted();
            }
        }
    };

    public RecordStateManager(Game game) {
        this.map = game.getMap();
        game.subscribeStateChanged(gameStateListener);
    }

    public void captureState() {
        Long delta = System.currentTimeMillis() - lastTimePushed;
        RecordState recordState = new RecordState(delta, map);
        System.out.println("State captured");

        lastStateCaptured++;
        recordStateMap.put(lastStateCaptured, recordState);
    }

    /* Call this after every new turn, effectively normalising the time between states,
    as players can plan their routes for an indefinite time */
    private void turnAnimationsStarted() {
        lastTimePushed = System.currentTimeMillis();
        captureState();
    }
}

