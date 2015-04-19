package gamelogic.replay;

import gamelogic.game.GameEvent;

public class ReplayEvent {
    GameEvent gameEvent;
    Object object;

    public ReplayEvent(GameEvent gameEvent, Object object) {
        this.gameEvent = gameEvent;
        this.object = object;
    }

    public ReplayEvent(GameEvent gameEvent) {
        this(gameEvent, null);
    }
}