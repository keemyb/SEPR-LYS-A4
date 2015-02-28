package gamelogic.resource;

import fvs.taxe.actor.TrainActor;
import gamelogic.game.Game;
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
    private Station finalStation;
    private List<Station> route = new ArrayList<>();                   // must not contain current position
    private List<Tuple<Station, Integer>> history = new ArrayList<>();   // station name and turn number


    public Train(String name, int speed) {
        this.name = name;
        this.speed = speed;
    }

    public Train(Train train) {
        this(train.name, train.speed);
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

    public List<Station> getRoute() {
        return route;
    }

    public void setRoute(List<Station> route) {
        if (route != null && route.size() > 0) {
            finalStation = route.get(route.size() - 1);
        }
        this.route = route;
    }

    public void arrivedAtDestination() {
        finalStation = null;
    }

    public Station getFinalStation() {
        return finalStation;
    }

    /**
     * This method returns the list of tuples representing where this train has travelled.
     * The first object in the tuple is the station visited, and the second object
     * is the turn that this train travelled to that station.
     * @return The train's history.
     */
    public List<Tuple<Station, Integer>> getHistory() {
        return history;
    }

    /**
     * This method finds whether a train has travelled to a certain station on/after
     * a specified turn.
     * @param station The station that you want to check
     * @param turn The minimum turn to consider
     * @return true if the station appears in the history, on or after the turn specified
     */
    public boolean historyContains(Station station, int turn) {
        for(Tuple<Station, Integer> entry: history) {
            if(entry.getFirst().equals(station) && entry.getSecond() >= turn) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the last turn a station was visited.
     * @param station The station to find.
     * @return The turn it was last visited, otherwise null if it was not visited.
     */
    public Integer getLastTurnStationWasVisited(Station station) {
        for (int i = history.size() - 1; i >= 0; i--) {
            Tuple<Station, Integer> entry = history.get(i);
            if (station.equals(entry.getFirst())) {
                return entry.getSecond();
            }
        }
        return null;
    }

    /**
     * Adds (station, turn) as the last entry to train history
     *
     * @param station the station visited
     * @param turn    number of turn when station was visited
     */
    public void addToHistory(Station station, int turn) {
        history.add(new Tuple<>(station, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }
}
