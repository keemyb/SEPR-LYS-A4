package gamelogic.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actions.SetNextMoveAction;
import fvs.taxe.actor.JellyActor;
import fvs.taxe.actor.TrainActor;
import gamelogic.Game;

/**
 * Created by Owen on 11/02/2015.
 */
public class Jelly {

    private Position position;
    private JellyActor actor;
    private Station nextStation;
    private final int speed = 40;

    public Jelly(Station station) {
        this.nextStation = station;
    }

    public JellyActor getActor() {
        return actor;
    }

    public void setActor(JellyActor actor) {
        this.actor = actor;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public void setNextStation(Station station) {
        nextStation = station;
        SequenceAction seq = new SequenceAction();
        float duration = getDistance(position, station.getLocation()) / speed;
        seq.addAction(Actions.moveTo(station.getLocation().getX() - JellyActor.width / 2, station.getLocation().getY() - JellyActor.height / 2, duration));
        seq.addAction(new SetNextMoveAction(this));
        actor.addAction(seq);
    }

    private float getDistance(Position a, Position b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
