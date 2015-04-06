package gamelogic.recording;

import java.util.HashMap;
import java.util.Map;

public class RecordStateManager {
    public Map<Integer, RecordState> recordStateMap = new HashMap<>();

    private Integer lastStatePushed = 0;

    private Long lastTimePushed = System.currentTimeMillis();

    private gamelogic.map.Map map;

    public RecordStateManager(gamelogic.map.Map map) {
        this.map = map;
    }

    public void captureState() {
        Long delta = System.currentTimeMillis() - lastTimePushed;
        RecordState recordState = new RecordState(delta, map);

        lastStatePushed++;
        recordStateMap.put(lastStatePushed, recordState);
    }

    /* Call this after every new turn, effectively normalising the time between states,
    as players can plan their routes for an indefinite time */
    public void turnStarted() {
        lastTimePushed = System.currentTimeMillis();
    }
}

