package gamelogic.resource;

import fvs.taxe.actor.TrainActor;
import gamelogic.Game;
import gamelogic.map.Position;
import gamelogic.map.Station;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents trains in the game.
 */
public class Train extends Resource {

    private TrainActor actor;
    private int speed;
    private Position position;
    private Position finalPosition;                 // must be set to null after arrival event
    private Station finalStation;                   // must correspond to finalPosition
    private List<Position> route;                   // must not contain current position
    private List<Tuple<String, Integer>> history;   // station name and turn number


    public Train(String name, int speed) {
        this.name = name;
        this.speed = speed;
        history = new ArrayList<>();
        route = new ArrayList<>();
    }

    public int getSpeed() {
        return speed;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        changed();
    }

    public TrainActor getActor() {
        return actor;
    }

    public void setActor(TrainActor actor) {
        this.actor = actor;
    }

    public List<Position> getRoute() {
        return route;
    }

    public void setRoute(List<Position> route) {
        if (route != null && route.size() > 0) {
            finalPosition = route.get(route.size() - 1);
            finalStation = Game.getInstance().getMap().getStationByPosition(finalPosition);
        }
        this.route = route;
    }

    public void arrivedAtDestination() {
        finalPosition = null;
        finalStation = null;
    }

    public Position getFinalPosition() {
        return finalPosition;
    }

    public Station getFinalStation() {
        return finalStation;
    }

    /**
     * Returns the list of stations and the number of turns when they were visited.
     *
     * @return
     */
    public List<Tuple<String, Integer>> getHistory() {
        return history;
    }

    /**
     * Adds (stationName, turn) as the last entry to train history
     *
     * @param stationName name of station
     * @param turn        number of turn when station was visited
     */
    public void addToHistory(String stationName, int turn) {
        history.add(new Tuple<>(stationName, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }
}
