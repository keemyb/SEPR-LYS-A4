package gamelogic.map;

import fvs.taxe.actor.StationActor;

public class Station {
    private String name;
    private Position location;
    private StationActor actor;
    private int failureDuration;

    public Station(String name, Position location) {
        this.name = name;
        this.location = location;
        setFailureDuration(0);
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

    public void decrementDuration() {
        if (failureDuration > 0) failureDuration--;
    }

    public boolean isPassable() {
        return failureDuration == 0;
    }

    public int getFailureDuration() {
        return failureDuration;
    }

    public void setFailureDuration(int failureDuration) {
        this.failureDuration = failureDuration;
    }

}
