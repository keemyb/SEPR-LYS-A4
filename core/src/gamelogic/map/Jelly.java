package gamelogic.map;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actor.JellyActor;
import gamelogic.game.Game;
import gamelogic.player.PlayerManager;

import java.util.List;
import java.util.Random;

/**
 * This class represents jellies in the game. Jellies are small creatures that move randomly along routes and slow down
 * trains.
 */
public class Jelly {

    private Position position;
    private JellyActor actor;
    private Station nextStation;

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

    private void setNextStation(Station station) {
        int speed = 40 + (int) (30 * ((float)PlayerManager.getTurnNumber() / (float)Game.getInstance().totalTurns));

        nextStation = station;
        SequenceAction seq = new SequenceAction();
        float duration = Map.getDistance(position, station.getLocation()) / speed;
        seq.addAction(Actions.moveTo(station.getLocation().getX() - JellyActor.width / 2,
                station.getLocation().getY() - JellyActor.height / 2, duration));
        seq.addAction(nextMoveAction());
        actor.addAction(seq);
    }


    public void startMoving() {
        actor.addAction(nextMoveAction());
    }

    /**
     * This is action is called once jelly arrives at the station, and it choose next random station for jelly to move.
     *
     * @return
     */
    private Action nextMoveAction() {
        return new Action() {
            @Override
            public boolean act(float delta) {
                List<Station> stations = Game.getInstance().getMap().getAdjacentStations(getNextStation());
                int random = new Random().nextInt(stations.size());
                Station nextStation = stations.get(random);
                setNextStation(nextStation);
                return true;
            }
        };
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
