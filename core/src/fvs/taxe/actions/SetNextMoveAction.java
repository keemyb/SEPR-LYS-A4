package fvs.taxe.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import gamelogic.Game;
import gamelogic.map.Jelly;
import gamelogic.map.Station;

import java.util.List;
import java.util.Random;

/**
 * Created by Owen on 13/02/2015.
 */
public class SetNextMoveAction extends Action {

    private Jelly jelly;

    public SetNextMoveAction(Jelly jelly) {
        this.jelly = jelly;
    }

    public boolean act(float delta) {
        List<Station> stations = Game.getInstance().getMap().getAdjacentStations(jelly.getNextStation());
        int random = new Random().nextInt(stations.size());
        Station nextStation = stations.get(random);
        jelly.setNextStation(nextStation);
        return true;
    }

}
