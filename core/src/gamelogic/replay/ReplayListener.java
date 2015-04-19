package gamelogic.replay;

import gamelogic.game.GameEvent;

public interface ReplayListener {

    void replay(GameEvent event, Object object);

}
