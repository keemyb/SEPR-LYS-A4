package gamelogic.map;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import fvs.taxe.actor.JunctionActor;
import fvs.taxe.actor.StationActor;

import javax.swing.*;

public class Station {
    private String name;
    private String abbreviation;
    private Position location;
    private Group actor;
    private int failureDuration;

    public Station(String name, String abbreviation, Position location) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.location = location;
        setFailureDuration(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Position getLocation() {
        return location;
    }

    public void setLocation(Position location) {
        this.location = location;
    }

    public Group getActor() {
        return actor;
    }

    public void setActor(Group actor) {
        this.actor = actor;
    }

    public void decrementDuration() {
        if (failureDuration > 0) {
            failureDuration--;
            if (failureDuration == 0)
                if (this instanceof Junction) ((JunctionActor) this.getActor()).setBroken();
        }
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
