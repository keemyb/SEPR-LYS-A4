package gamelogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import fvs.taxe.controller.TrainMoveController;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    private List<List<Float>> distances;
    private Random random = new Random();

    public Map() {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();
        distances = new ArrayList<>();

        initialise();
    }

    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));

        parseStations(jsonVal);
        parseConnections(jsonVal);

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
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            for (JsonValue val = station.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if (val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if (val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            if (isJunction) {
                addJunction(name, new Position(x, y));
            } else {
                addStation(name, new Position(x, y));
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

    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, Position location) {
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }

    public Junction addJunction(String name, Position location) {
        Junction newJunction = new Junction(name, location);
        stations.add(newJunction);
        return newJunction;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Connection> getConnections() {
        return connections;
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

        throw new RuntimeException("Station does not exist for that position");
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
                }
                else if (doesConnectionExist(si.getName(), sj.getName())) {
                    Position loci = si.getLocation();
                    Position locj = sj.getLocation();
                    distances.get(i).add(Vector2.dst(loci.getX(), loci.getY(), locj.getX(), locj.getY()));
                }
                else {
                    distances.get(i).add(Float.MAX_VALUE);
                }
            }
        }

        // Execute Floyd-Warshall algorithm
        for (int k = 0; k < stations.size(); k++) {
            for (int i = 0; i < stations.size(); i++) {
                for (int j = 0; j < stations.size(); j++) {
                    if (distances.get(i).get(k) != Float.MAX_VALUE && distances.get(k).get(j) != Float.MAX_VALUE) {
                        if (distances.get(i).get(j) > distances.get(i).get(k) + distances.get(k).get(j)) {
                            distances.get(i).set(j, distances.get(i).get(k) + distances.get(k).get(j));
                        }
                    }
                }
            }
        }

        // Debug info: printing distances
//        for (int i = 0; i < stations.size(); i++) {
//            for (int j = 0; j < stations.size(); j++) {
//                System.out.println(stations.get(i).getName() + " > " +
//                        stations.get(j).getName() + ": " +
//                        distances.get(i).get(j));
//            }
//        }
    }

}
