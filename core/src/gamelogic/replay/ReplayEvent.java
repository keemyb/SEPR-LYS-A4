package gamelogic.replay;

import gamelogic.game.GameEvent;

public class ReplayEvent {
    GameEvent gameEvent;
    Object object;
    /* The associated object, used when an event is ambiguous without it.
    For example the SELECTED_TRAIN event needs to know what train was selected.
    Search for usages of a particular event to find out if it requires an event.
     */

    public ReplayEvent(GameEvent gameEvent, Object object) {
        this.gameEvent = gameEvent;
        this.object = object;
    }

    public ReplayEvent(GameEvent gameEvent) {
        this(gameEvent, null);
    }
}