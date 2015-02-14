package fvs.taxe.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import gamelogic.map.Junction;
import gamelogic.map.Station;

/**
 * Created by Owen on 29/01/2015.
 */
public class WaitUntilPassableAction extends Action {

    private Junction junction;

    public WaitUntilPassableAction(Junction junction) {
        this.junction = junction;
    }

    public boolean act(float delta) {
        return junction.isPassable();
    }

}
