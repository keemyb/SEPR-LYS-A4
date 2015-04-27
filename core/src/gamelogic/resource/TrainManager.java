package gamelogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gamelogic.game.Game;
import gamelogic.game.GameEvent;
import gamelogic.player.Player;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;

import java.util.*;

/**
 * Abstract class providing functions for manipulating trains.
 */
public abstract class TrainManager {
    public final static int CONFIG_MAX_TRAINS = 7;
    private final static Random random = new Random();
    private static final String DEFAULT_TRAIN_NAME = "NO NAME";
    private static final int DEFAULT_TRAIN_SPEED = 50;

    public static List<Train> trains = new ArrayList<Train>();

    //Reading trains from json-file.
    static {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("trains.json"));

        for(JsonValue train = jsonVal.getChild("trains"); train != null; train = train.next()) {
            // If no name or speed is found these defaults will be used.
            String name = DEFAULT_TRAIN_NAME;
            int speed = DEFAULT_TRAIN_SPEED;
            for(JsonValue val  = train.child; val != null; val = val.next()) {
                if(val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else {
                    speed = val.asInt();
                }
            }
            Train newTrain = new Train(name, speed);
            trains.add(newTrain);
        }
    }

    public static int getFastestTrainSpeed() {
        int fastestTrainSpeed = 0;
        for (Train train : trains) {
            int trainSpeed = train.getSpeed();
            if (trainSpeed > fastestTrainSpeed) {
                fastestTrainSpeed = trainSpeed;
            }
        }
        return fastestTrainSpeed;
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
        Train train = trains.get(index);
        return new Train(train);
    }

    /**
     * Add a random train to a player. The trains that can be received are split into 3 phases <ol> <li>Phase zero: 70%
     * Steam; 20% Electric; 10% Diesel</li> <li>Phase one: 25% Steam; 35% Electric; 25% Diesel; 15% Petrol</li>
     * <li>Phase two: 0% Steam; 10% Electric; 20% Diesel; 40% Petrol; 30% Bullet</li> </ol>
     *
     * A player can own a maximum of 3 trains of a single type
     *
     * @param player the player that will receive the train
     */
    public static void addRandomTrainToPlayer(Player player) {
        Train train = getRandomTrain();
        int count = 0;
        for (Train testTrain : player.getTrains()){
            if (testTrain.getName().equals(train.getName())){
                count++;
            }
        }
        if (count < 3){
            addTrainToPlayer(player, train);
        }else{
            addRandomTrainToPlayer(player);
        }

    }

    public static void addTrainToPlayer(Player player, Train train) {
        if (player.getTrains().size() >= CONFIG_MAX_TRAINS) {
            return;
        }

        train.setPlayer(player);
        player.addTrain(train);

        EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.ADD_TRAIN, new ArrayList<>(
                Arrays.asList(player, train)
        )));
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