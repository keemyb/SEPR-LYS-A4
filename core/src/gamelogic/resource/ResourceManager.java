package gamelogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gamelogic.Player;
import util.Tuple;

import java.util.ArrayList;
import java.util.Random;

public class ResourceManager {
    public final int CONFIG_MAX_RESOURCES = 7;
    private Random random = new Random();
    private ArrayList<Tuple<String, Integer>> trains;

    public ResourceManager() {
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

    private Resource getRandomResource() {

        int index = random.nextInt(trains.size());
        Tuple<String, Integer> train = trains.get(index);
        String leftImage = train.getFirst().toLowerCase().replaceAll(" ", "-") + ".png";
        String rightImage = train.getFirst().toLowerCase().replaceAll(" ", "-") + "-right.png";
        return new Train(train.getFirst(), leftImage, rightImage, train.getSecond());

    }

    public void addRandomResourceToPlayer(Player player) {
        addResourceToPlayer(player, getRandomResource());
    }

    private void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() >= CONFIG_MAX_RESOURCES) {
            return;
        }

        resource.setPlayer(player);
        player.addResource(resource);
    }
}