package gamelogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gamelogic.Game;
import gamelogic.Player;
import util.Tuple;

import java.util.ArrayList;
import java.util.Random;

public class TrainManager {
    public final int CONFIG_MAX_TRAINS = 7;
    private Random random = new Random();
    private ArrayList<Tuple<String, Integer>> trains;

    public TrainManager() {
        initialise();
    }

    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("trains.json"));

        trains = new ArrayList<Tuple<String, Integer>>();
        for (JsonValue train = jsonVal.getChild("trains"); train != null; train = train.next()) {
            String name = "";
            int speed = 50;
            for (JsonValue val = train.child; val != null; val = val.next()) {
                if (val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else {
                    speed = val.asInt();
                }
            }
            trains.add(new Tuple<String, Integer>(name, speed));
        }
    }

    public ArrayList<String> getTrainNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Tuple<String, Integer> train : trains) {
            names.add(train.getFirst());
        }
        return names;
    }

    public ArrayList<Tuple<String, Integer>> getTrains() {
        return trains;
    }

    private Train getRandomTrain() {
        /*
        The trains that can be received are split into 3 phases
        Phase 0: 70% Steam; 20% Electric; 10% Diesel
        Phase 1: 25% Steam; 35% Electric; 25% Diesel; 15% Petrol
        Phase 2: 0% Steam; 10% Electric; 20% Diesel; 40% Petrol; 30% BULLLLLLET!!!!OMG KAPPA
         */

        float turn = Game.getInstance().getPlayerManager().getTurnNumber();
        int phase = (int) Math.floor((turn / Game.getInstance().TOTAL_TURNS) * 3.0);
        double randDouble = random.nextDouble();
        int index;

        // very much hard coded random train selection
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
        Tuple<String, Integer> train = trains.get(index);
        String leftImage = train.getFirst().toLowerCase().replaceAll(" ", "-") + ".png";
        String rightImage = train.getFirst().toLowerCase().replaceAll(" ", "-") + "-right.png";
        return new Train(train.getFirst(), leftImage, rightImage, train.getSecond());

    }

    public void addRandomTrainToPlayer(Player player) {
        addTrainToPlayer(player, getRandomTrain());
    }

    private void addTrainToPlayer(Player player, Train train) {
        if (player.getResources().size() >= CONFIG_MAX_TRAINS) {
            return;
        }

        train.setPlayer(player);
        player.addResource(train);
    }
}