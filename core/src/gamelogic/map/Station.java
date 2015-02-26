package gamelogic.map;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * This class represents stations in the game.
 */
public class Station {
    private String name;
    private Position location;
    private Group actor;

    public Station(String name, Position location) {
        this.name = name;
        this.location = location;
    }

    public static float getDistance(Station station1, Station station2) {
        return Position.getDistance(station1.location, station2.location);
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

    public Group getActor() {
        return actor;
    }

    public void setActor(Group actor) {
        this.actor = actor;
    }

}
