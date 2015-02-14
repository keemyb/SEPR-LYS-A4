package gamelogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import fvs.taxe.actor.JunctionActor;
import gamelogic.Game;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    private List<Jelly> jellies;
    private List<List<Float>> distances;
    private Random random = new Random();
    private final double JUNCTION_FAILURE_RATE = 0.2;
    private final int JUNCTION_FAILURE_DURATION = 4;

    public Map() {
        stations = new ArrayList<>();
        connections = new ArrayList<>();
        jellies = new ArrayList<>();
        distances = new ArrayList<>();

        initialise();
    }

    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));

        parseStations(jsonVal);
        parseConnections(jsonVal);

        addJelly();
        addJelly();
        addJelly();
        addJelly();

        computeDistances();
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
            String name = "";
            String abbreviation = "";
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            for (JsonValue val = station.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if (val.name.equalsIgnoreCase("abbreviation")) {
                    abbreviation = val.asString();
                } else if (val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if (val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            if (isJunction) {
                addJunction(name, abbreviation, new Position(x, y));
            } else {
                addStation(name, abbreviation, new Position(x, y));
            }
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

    public boolean doesConnectionExist(Position a, Position b) {
        for (Connection connection: connections) {
            Position p1 = connection.getStation1().getLocation();
            Position p2 = connection.getStation2().getLocation();
            double dist1 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(), p2.getY(), a.getX(), a.getY());
            double dist2 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(), p2.getY(), b.getX(), b.getY());
            if (dist1 <= 0.0001 && dist2 <= 0.0001)
                return true;
            System.out.println(dist1 + " ! " + dist2);
        }
        return false;
    }

    public Jelly addJelly() {
        Station station = stations.get(new Random().nextInt(stations.size()));
        Jelly jelly = new Jelly(station);
        jelly.setPosition(station.getLocation());
        jellies.add(jelly);

        return jelly;
    }

    public void handleJellyCollisions(){
        for (Jelly jelly : jellies) {
            //HANDLE COLLISIONS
        }
    }

    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, String abbreviation, Position location) {
        Station newStation = new Station(name,abbreviation, location);
        stations.add(newStation);
        return newStation;
    }

    public Junction addJunction(String name, String abbreviation, Position location) {
        Junction newJunction = new Junction(name, abbreviation, location);
        stations.add(newJunction);
        return newJunction;
    }

    public void handleJunctionFailures() {
        ArrayList<Junction> junctions = new ArrayList<>();
        for (Station s : stations) {
            if (s instanceof Junction){
                if (s.isPassable()) junctions.add((Junction) s);
                else s.decrementDuration();
            }
        }
        breakRandomJunction(junctions);
    }

    public void breakRandomJunction(ArrayList<Junction> junctions) {
        if (random.nextDouble() <= JUNCTION_FAILURE_RATE) {
            // Select one of the junctions to break
            if (junctions.size() > 0) {
                Junction junction = junctions.get(random.nextInt(junctions.size()));
                breakStation(junction, JUNCTION_FAILURE_DURATION);
                System.out.println(junction.getName() + " has failed! Oh noes!");
            }
        }
    }

    public void breakStation(Station station, int duration) {
        station.setFailureDuration(duration);
        if (station instanceof Junction) ((JunctionActor) station.getActor()).setBroken();
    }

    public void fixStation(Station station) {
        station.setFailureDuration(0);
        if (station instanceof Junction) ((JunctionActor) station.getActor()).setDefault();
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

    //Add Connection by Names
    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }

    //Get connections from station
    public List<Connection> getConnectionsFromStation(Station station) {
        List<Connection> results = new ArrayList<Connection>();
        for (Connection connection : connections) {
            if (connection.getStation1() == station || connection.getStation2() == station) {
                results.add(connection);
            }
        }
        return results;
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

    public Station getStationFromPosition(Position position) {
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }
        return null;
    }

    public List<Station> createRoute(List<Position> positions) {
        List<Station> route = new ArrayList<Station>();

        for (Position position : positions) {
            route.add(getStationFromPosition(position));
        }

        return route;
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

//        //Debug info: printing distances
//        for (int i = 0; i < stations.size(); i++) {
//            for (int j = 0; j < stations.size(); j++) {
//                System.out.println(stations.get(i).getName() + " > " +
//                        stations.get(j).getName() + ": " +
//                        distances.get(i).get(j));
//            }
//        }
    }

    public float getDistance(Station a, Station b) {
        return distances.get(stations.indexOf(a)).get(stations.indexOf(b));
    }

}
