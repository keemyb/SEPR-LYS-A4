package gamelogic.replay;

import gamelogic.game.GameEvent;

public interface ReplayListener {

    public void replay(GameEvent event, Object object);

}
