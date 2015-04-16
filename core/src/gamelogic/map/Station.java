package gamelogic.map;

import com.badlogic.gdx.scenes.scene2d.Group;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.util.ArrayList;

/**
 * This class represents stations in the game.
 */
public class Station {
    private String name;
    private Position location;
    private Group actor;
    private ArrayList<Train> trainsAtStation = new ArrayList<>();

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

    public ArrayList<Train> getTrainsAtStation() {
        trainsAtStation.clear();
        for (Player player : PlayerManager.getAllPlayers()) {
            for (Resource resource : player.getTrains()) {
                Train train = (Train) resource;
                System.out.println("-----Train Name: " + train.getName() + "------Train Location: " + train.getLocation());
                if (train.getLocation() == this) {
                    trainsAtStation.add(train);
                }
            }
        }
        System.out.println("Trains at station: " + trainsAtStation);
        return trainsAtStation;
    }

}
