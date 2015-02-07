package gamelogic.resource;

import fvs.taxe.actor.TrainActor;
import gamelogic.Game;
import gamelogic.map.Position;
import gamelogic.map.Station;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Train extends Resource {
    private String leftImage;
    private String rightImage;
    private Position position;
    private TrainActor actor;
    private int speed;
    // Final destination should be set to null after firing the arrival event
    private Position finalDestination;

    // Should NOT contain current position!
    private List<Position> route;

    //Station name and turn number
    private List<Tuple<String, Integer>> history;


    public Train(String name, String leftImage, String rightImage, int speed) {
        this.name = name;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.speed = speed;
        history = new ArrayList<>();
        route = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getLeftImage() {
        return "trains/" + leftImage;
    }

    public String getRightImage() {
        return "trains/" + rightImage;
    }

    public String getCursorImage() {
        return "trains/cursor/" + leftImage;
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

    public boolean isMoving() {
        return finalDestination != null;
    }

    public List<Position> getRoute() {
        return route;
    }

    public void setRoute(List<Position> route) {
        // Final destination should be set to null after firing the arrival event
        if (route != null && route.size() > 0)
            finalDestination = route.get(route.size() - 1);

        this.route = route;
    }

    public Position getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Position position) {
        finalDestination = position;
    }

    public Station getFinalStation() {
        Station s = Game.getInstance().getMap().getStationFromPosition(finalDestination);
        if (s != null) return s;
        throw new RuntimeException("train's route must end with a station");
    }

    public int getSpeed() {
        return speed;
    }

    //Station name and turn number
    public List<Tuple<String, Integer>> getHistory() {
        return history;
    }

    //Station name and turn number
    public void addHistory(String stationName, int turn) {
        history.add(new Tuple<>(stationName, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }
}
