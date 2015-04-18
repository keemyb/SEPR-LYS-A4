package gamelogic.replay;

import gamelogic.game.GameEvent;

public class ReplayEvent {
    GameEvent gameEvent;
    Object object;
    long time;

    public ReplayEvent(GameEvent gameEvent, Object object) {
        this.gameEvent = gameEvent;
        this.object = object;
        this.time = System.currentTimeMillis();
    }
}