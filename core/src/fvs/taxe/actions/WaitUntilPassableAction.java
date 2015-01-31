package fvs.taxe.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import gamelogic.map.Station;

/**
 * Created by Owen on 29/01/2015.
 */
public class WaitUntilPassableAction extends Action {

    private Station station;

    public WaitUntilPassableAction(Station station) {
        this.station = station;
    }

    public boolean act(float delta) {
        if (station.isPassable()) return true;
        else return false;
    }

}
