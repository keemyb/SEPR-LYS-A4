package gamelogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.awt.*;
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
	private final int NUMBER_OF_JELLIES = 4;
	private List<Station> stations;
	private List<Junction> junctions;
	private List<Connection> connections;
	private List<List<Float>> distances;
	private Random random = new Random();

	public Map() {
		stations = new ArrayList<>();
		junctions = new ArrayList<>();
		connections = new ArrayList<>();
		distances = new ArrayList<>();

		JsonReader jsonReader = new JsonReader();
		JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));
		parseStations(jsonVal);
		parseConnections(jsonVal);

		for (Station s : stations)
			if (s instanceof Junction)
				junctions.add((Junction) s);

		computeDistances();
	}

	/**
	 * Returns Euclidean distance between two points.
	 *
	 * @param a
	 *            position of the first point
	 * @param b
	 *            position of the second point
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
            // Pre-defined connections are Gold.
			addConnection(station1, station2, Connection.Material.GOLD);
		}
	}

	private void parseStations(JsonValue jsonVal) {
		for (JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
			String name = "";
			int x = 0, y = 0;
			boolean isJunction = false;
			for (JsonValue val = station.child; val != null; val = val.next) {
				if (val.name.equalsIgnoreCase("name"))
					name = val.asString();
				else if (val.name.equalsIgnoreCase("x"))
					x = val.asInt();
				else if (val.name.equalsIgnoreCase("y"))
					y = val.asInt();
				else
					isJunction = val.asBoolean();
			}
			if (isJunction)
				addJunction(name, new Position(x, y));
			else
				addStation(name, new Position(x, y));
		}
	}

	public boolean doesConnectionExist(String stationName,
			String anotherStationName) {
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
	 * @param a
	 *            first position
	 * @param b
	 *            second position
	 * @return true if there's a connection going through line (a, b); false
	 *         otherwise
	 */
	public boolean doesConnectionExist(Position a, Position b) {
		for (Connection connection : connections) {
			Position p1 = connection.getStation1().getLocation();
			Position p2 = connection.getStation2().getLocation();
			double dist1 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(),
					p2.getY(), a.getX(), a.getY());
			double dist2 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(),
					p2.getY(), b.getX(), b.getY());
			if (dist1 <= 0.0001 && dist2 <= 0.0001)
				return true;
		}
		return false;
	}

    public boolean prospectiveConnectionIsValid(Connection prospectiveConnection) {
        Station station1 = prospectiveConnection.getStation1();
        Station station2 = prospectiveConnection.getStation2();
        if (doesConnectionExist(station1.getName(), station2.getName())) return false;
        if (doesProspectiveConnectionIntersectExisting(prospectiveConnection)) return false;
        return true;
    }

    public boolean doesProspectiveConnectionIntersectExisting(Connection prospectiveConnection) {
        Line2D prospectiveLine = connectionToLine(prospectiveConnection);
        for (Connection connection : connections) {
            if (connection.hasCommonStation(prospectiveConnection)) continue;

            Line2D existingLine = connectionToLine(connection);
            if (existingLine.intersectsLine(prospectiveLine)) return true;
        }
        return false;
    }

    public Line2D connectionToLine(Connection connection) {
        return new Line2D.Double(connection.getStation1().getLocation().getX(),
                connection.getStation1().getLocation().getY(),
                connection.getStation2().getLocation().getX(),
                connection.getStation2().getLocation().getY());
    }

	public Station getRandomStation() {
		return stations.get(random.nextInt(stations.size()));
	}

	public Station addStation(String name, Position location) {
		Station station = new Station(name, location);
		stations.add(station);
		return station;
	}

	public Junction addJunction(String name, Position location) {
		Junction junction = new Junction(name, location);
		stations.add(junction);
		junctions.add(junction);
		return junction;
	}

	/**
	 * Decrements failure duration of already failed junctions and randomly
	 * selects the next junction to fail.
	 */
	public void handleJunctionFailures() {
		List<Junction> breakable = new ArrayList<>();
		for (Junction junction : junctions) {
			if (junction.isPassable())
				breakable.add(junction);
			else
				junction.decrementFailureDuration();
		}
		breakRandomJunction(breakable);
	}

	private void breakRandomJunction(List<Junction> junctions) {
		if (random.nextDouble() <= JUNCTION_FAILURE_RATE
				&& !junctions.isEmpty()) {
			Junction junction = junctions.get(random.nextInt(junctions.size()));
			junction.setFailureDuration(JUNCTION_FAILURE_DURATION);
			System.out.println(junction.getName() + " has failed! Oh noes!");
		}
	}

	public List<Station> getStations() {
		return stations;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public List<Station> getAdjacentStations(Station station) {
		List<Station> adjacentStations = new ArrayList<>();
		for (Connection c : connections) {
			if (c.getStation1() == station)
				adjacentStations.add(c.getStation2());
			else if (c.getStation2() == station)
				adjacentStations.add(c.getStation1());
		}
		return adjacentStations;
	}

	public Connection addConnection(Station station1, Station station2, Connection.Material material) {
		Connection newConnection = new Connection(station1, station2, material);
		connections.add(newConnection);
		return newConnection;
	}

	public Connection addConnection(String stationName1, String stationName2, Connection.Material material) {
		Station station1 = getStationByName(stationName1);
		Station station2 = getStationByName(stationName2);
		return addConnection(station1, station2, material);
	}

	public Station getStationByName(String name) {
        for (Station station : stations) {
            if (station.getName().equals(name)) {
                return station;
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
	 * Uses Floyd-Warshall algorithm to compute shortest distances between every
	 * pair of stations.
	 */
	private void computeDistances() {
		// Setting all initial distances to infinity or 0, if stations are /
		// aren't the same
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
					distances.get(i).add(
							Vector2.dst(loci.getX(), loci.getY(), locj.getX(),
									locj.getY()));
				} else {
					distances.get(i).add(Float.MAX_VALUE);
				}
			}
		}
		// Execute Floyd-Warshall algorithm
		for (int k = 0; k < stations.size(); k++)
			for (int i = 0; i < stations.size(); i++)
				for (int j = 0; j < stations.size(); j++)
					if (distances.get(i).get(k) != Float.MAX_VALUE
							&& distances.get(k).get(j) != Float.MAX_VALUE)
						if (distances.get(i).get(j) > distances.get(i).get(k)
								+ distances.get(k).get(j)) {
							distances.get(i).set(
									j,
									distances.get(i).get(k)
											+ distances.get(k).get(j));
						}
	}

	/**
	 * Returns length of shortest route between two stations.
	 *
	 * @param a
	 *            first station
	 * @param b
	 *            second station
	 * @return length of shortest route between a and b.
	 */
	public float getShortestRouteDistance(Station a, Station b) {
		return distances.get(stations.indexOf(a)).get(stations.indexOf(b));
	}

}
