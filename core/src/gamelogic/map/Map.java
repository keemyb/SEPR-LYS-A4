package gamelogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import fvs.taxe.actor.JellyActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.TopBarController;
import gamelogic.game.Game;
import gamelogic.player.Player;
import gamelogic.player.PlayerManager;
import gamelogic.resource.Resource;
import gamelogic.resource.Train;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents map in the game, containing stations and jellies.
 */
public class Map {
    private final double JUNCTION_FAILURE_RATE = 0.2;
    private final int JUNCTION_FAILURE_DURATION = 4;
    private List<Station> stations;
    private List<Junction> junctions;
    private List<Connection> connections;
    private List<Jelly> jellies;
    private List<List<Float>> distances;
    private Random random = new Random();

    public Map() {
        stations = new ArrayList<>();
        junctions = new ArrayList<>();
        connections = new ArrayList<>();
        jellies = new ArrayList<>();
        distances = new ArrayList<>();

        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));
        parseStations(jsonVal);
        parseConnections(jsonVal);

        for (Station s : stations)
            if (s instanceof Junction)
                junctions.add((Junction) s);

        computeDistances();
        addJelly();
        addJelly();
        addJelly();
        addJelly();
    }

    /**
     * Returns Euclidean distance between two points.
     *
     * @param a position of the first point
     * @param b position of the second point
     * @return Euclidean distance between a and b
     */
    public static float getDistance(Position a, Position b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private void parseConnections(JsonValue jsonVal) {
        for (JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";
            for (JsonValue val = connection.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }
            addConnection(station1, station2);
        }
    }

    private void parseStations(JsonValue jsonVal) {
        for (JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
            String name = "", abbreviation = "";
            int x = 0, y = 0;
            boolean isJunction = false;
            for (JsonValue val = station.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("name")) name = val.asString();
                else if (val.name.equalsIgnoreCase("abbreviation")) abbreviation = val.asString();
                else if (val.name.equalsIgnoreCase("x")) x = val.asInt();
                else if (val.name.equalsIgnoreCase("y")) y = val.asInt();
                else isJunction = val.asBoolean();
            }
            if (isJunction) addJunction(name, abbreviation, new Position(x, y));
            else addStation(name, abbreviation, new Position(x, y));
        }
    }

    public boolean doesConnectionExist(String stationName, String anotherStationName) {
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();
            if (s1.equals(stationName) && s2.equals(anotherStationName)
                    || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if there exists a connection going through two given positions
     *
     * @param a first position
     * @param b second position
     * @return true if there's a connection going through line (a, b); false otherwise
     */
    public boolean doesConnectionExist(Position a, Position b) {
        for (Connection connection : connections) {
            Position p1 = connection.getStation1().getLocation();
            Position p2 = connection.getStation2().getLocation();
            double dist1 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(), p2.getY(), a.getX(), a.getY());
            double dist2 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(), p2.getY(), b.getX(), b.getY());
            if (dist1 <= 0.0001 && dist2 <= 0.0001)
                return true;
        }
        return false;
    }

    public Jelly addJelly() {
        Station station = stations.get(random.nextInt(stations.size()));
        Jelly jelly = new Jelly(station);
        jelly.setPosition(station.getLocation());
        jellies.add(jelly);
        return jelly;
    }

    public void handleJellyCollisions() {
        for (Jelly jelly : jellies) {
            JellyActor jellyActor = jelly.getActor();
            Rectangle bounds = jellyActor.getBounds();
            for (Player player : PlayerManager.getAllPlayers()) {
                ArrayList<Resource> toDelete = new ArrayList<>();
                for (Resource r : player.getResources()) {
                    if (r instanceof Train) {
                        Position position = ((Train) r).getPosition();
                        if (position != null) {
                            if (bounds.contains(position.getX(), position.getY())) {
                                toDelete.add(r);

                            }
                        }
                    }
                }
                for (Resource r : toDelete) {
                    player.removeResource(r);
                }
            }
        }
    }

    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, String abbreviation, Position location) {
        Station station = new Station(name, abbreviation, location);
        stations.add(station);
        return station;
    }

    public Junction addJunction(String name, String abbreviation, Position location) {
        Junction junction = new Junction(name, abbreviation, location);
        stations.add(junction);
        junctions.add(junction);
        return junction;
    }

    /**
     * Decrements failure duration of already failed junctions and randomly selects the next junction to fail.
     */
    public void handleJunctionFailures() {
        List<Junction> breakable = new ArrayList<>();
        for (Junction junction : junctions) {
            if (junction.isPassable()) breakable.add(junction);
            else junction.decrementFailureDuration();
        }
        breakRandomJunction(breakable);
    }

    private void breakRandomJunction(List<Junction> junctions) {
        if (random.nextDouble() <= JUNCTION_FAILURE_RATE && !junctions.isEmpty()) {
            Junction junction = junctions.get(random.nextInt(junctions.size()));
            junction.setFailureDuration(JUNCTION_FAILURE_DURATION);
            System.out.println(junction.getName() + " has failed! Oh noes!");
        }
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Jelly> getJellies() {
        return jellies;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<Station> getAdjacentStations(Station station) {
        List<Station> adjacentStations = new ArrayList<>();
        for (Connection c : connections) {
            if (c.getStation1() == station) adjacentStations.add(c.getStation2());
            else if (c.getStation2() == station) adjacentStations.add(c.getStation1());
        }
        return adjacentStations;
    }

    public Connection addConnection(Station station1, Station station2) {
        Connection newConnection = new Connection(station1, station2);
        connections.add(newConnection);
        return newConnection;
    }

    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }

    public Station getStationByName(String name) {
        int i = 0;
        while (i < stations.size()) {
            if (stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else {
                i++;
            }
        }
        return null;
    }

    public Station getStationByPosition(Position position) {
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Uses Floyd-Warshall algorithm to compute shortest distances between every pair of stations.
     */
    private void computeDistances() {
        // Setting all initial distances to infinity or 0, if stations are / aren't the same
        for (int i = 0; i < stations.size(); i++) {
            distances.add(new ArrayList<Float>());
            for (int j = 0; j < stations.size(); j++) {
                Station si = stations.get(i);
                Station sj = stations.get(j);
                if (i == j) {
                    distances.get(i).add(0f);
                } else if (doesConnectionExist(si.getName(), sj.getName())) {
                    Position loci = si.getLocation();
                    Position locj = sj.getLocation();
                    distances.get(i).add(Vector2.dst(loci.getX(), loci.getY(), locj.getX(), locj.getY()));
                } else {
                    distances.get(i).add(Float.MAX_VALUE);
                }
            }
        }
        // Execute Floyd-Warshall algorithm
        for (int k = 0; k < stations.size(); k++)
            for (int i = 0; i < stations.size(); i++)
                for (int j = 0; j < stations.size(); j++)
                    if (distances.get(i).get(k) != Float.MAX_VALUE && distances.get(k).get(j) != Float.MAX_VALUE)
                        if (distances.get(i).get(j) > distances.get(i).get(k) + distances.get(k).get(j)) {
                            distances.get(i).set(j, distances.get(i).get(k) + distances.get(k).get(j));
                        }
    }

    /**
     * Returns length of shortest route between two stations.
     *
     * @param a first station
     * @param b second station
     * @return length of shortest route between a and b.
     */
    public float getShortestRouteDistance(Station a, Station b) {
        return distances.get(stations.indexOf(a)).get(stations.indexOf(b));
    }

}
