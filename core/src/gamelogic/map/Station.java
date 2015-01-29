package gamelogic.map;

import fvs.taxe.actor.StationActor;

public class Station {
    private String name;
    private Position location;
    private StationActor actor;

    public Station(String name, Position location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getLocation() {
        return location;
    }

    public void setLocation(Position location) {
        this.location = location;
    }

    public StationActor getActor() {
        return actor;
    }

    public void setActor(StationActor actor) {
        this.actor = actor;
    }

}
