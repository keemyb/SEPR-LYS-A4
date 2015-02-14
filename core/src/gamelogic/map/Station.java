package gamelogic.map;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * This class represents stations in the game.
 */
public class Station {
    private String name;
    private String abbreviation;
    private Position location;
    private Group actor;

    public Station(String name, String abbreviation, Position location) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.location = location;
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

    public Position getLocation() {
        return location;
    }

    public Group getActor() {
        return actor;
    }

    public void setActor(Group actor) {
        this.actor = actor;
    }

}
