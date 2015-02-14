package gamelogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gamelogic.Game;
import gamelogic.Player;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract class providing functions for manipulating trains.
 */
public abstract class TrainManager {
    public final static int CONFIG_MAX_TRAINS = 7;
    private final static Random random = new Random();
    private final static ArrayList<Tuple<String, Integer>> trainEntries;

    //Reading trains from json-file.
    static {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("trains.json"));

        trainEntries = new ArrayList<>();
        for (JsonValue entry = jsonVal.getChild("trains"); entry != null; entry = entry.next()) {
            String name = "";
            int speed = 0;
            for (JsonValue val = entry.child; val != null; val = val.next()) {
                if (val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else {
                    speed = val.asInt();
                }
            }
            trainEntries.add(new Tuple<>(name, speed));
        }
    }

    public static List<String> getTrainNames() {
        List<String> names = new ArrayList<>();
        for (Tuple<String, Integer> train : trainEntries) {
            names.add(train.getFirst());
        }
        return names;
    }

    public static Train getRandomTrain() {
        double randDouble = random.nextDouble();
        int phase = Game.getInstance().getPhase();
        int index;
        // Very much hard coded random train selection
        if (phase == 0) {
            if (randDouble < 0.7) index = 4;
            else if (randDouble < 0.9) index = 3;
            else index = 2;
        } else if (phase == 1) {
            if (randDouble < 0.25) index = 4;
            else if (randDouble < 0.6) index = 3;
            else if (randDouble < 0.85) index = 2;
            else index = 1;
        } else {
            if (randDouble < 0.1) index = 3;
            else if (randDouble < 0.3) index = 2;
            else if (randDouble < 0.7) index = 1;
            else index = 0;
        }

        // returns a train with the given index
        Tuple<String, Integer> entry = trainEntries.get(index);
        return new Train(entry.getFirst(), entry.getSecond());

    }

    /**
     * Add a random train to a player. The trains that can be received are split into 3 phases <ol> <li>Phase zero: 70%
     * Steam; 20% Electric; 10% Diesel</li> <li>Phase one: 25% Steam; 35% Electric; 25% Diesel; 15% Petrol</li>
     * <li>Phase two: 0% Steam; 10% Electric; 20% Diesel; 40% Petrol; 30% Bullet</li> </ol>
     *
     * @param player the player that will receive the train
     */
    public static void addRandomTrainToPlayer(Player player) {
        addTrainToPlayer(player, getRandomTrain());
    }

    public static void addTrainToPlayer(Player player, Train train) {
        if (player.getResources().size() >= CONFIG_MAX_TRAINS) {
            return;
        }

        train.setPlayer(player);
        player.addResource(train);
    }

    public static String getLeftImageFileName(Train train) {
        return "trains/" + train.getName().toLowerCase().replaceAll(" ", "-") + ".png";
    }

    public static String getRightImageFileName(Train train) {
        return "trains/" + train.getName().toLowerCase().replaceAll(" ", "-") + "-right.png";
    }

    public static String getCursorImageFileName(Train train) {
        return "trains/cursor/" + train.getName().toLowerCase().replaceAll(" ", "-") + ".png";
    }

}