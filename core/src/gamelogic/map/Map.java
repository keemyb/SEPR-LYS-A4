package gamelogic.map;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gamelogic.game.GameEvent;
import gamelogic.player.Player;
import gamelogic.replay.EventReplayer;
import gamelogic.replay.ReplayEvent;

import java.awt.geom.Line2D;
import java.util.*;

/**
 * This class represents map in the game, containing stations.
 */
public class Map {
	private final double JUNCTION_FAILURE_RATE = 0.2;
	private final int JUNCTION_FAILURE_DURATION = 4;
	private Junction lastBroken;
	private List<Station> stations;
	private java.util.Map<String, List<Station>> zones;
	private List<Junction> junctions;
	private List<Connection> connections;
	private List<Set<String>> invalidConnections;
	private List<List<Float>> distances;
	private Random random = new Random();

    private boolean initialised = false;

	public Map() {
		stations = new ArrayList<>();
		zones = new HashMap<>();
		junctions = new ArrayList<>();
		connections = new ArrayList<>();
		invalidConnections = new ArrayList<>();
		distances = new ArrayList<>();

		JsonReader jsonReader = new JsonReader();
		JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));
		parseStations(jsonVal);
		parseZones(jsonVal);
		parseConnections(jsonVal);
		parseInvalidConnection(jsonVal);
        for (Station s : stations)
			if (s instanceof Junction) {
                junctions.add((Junction) s);
            }

        computeDistances();
        initialised = true;
    }

	private void parseZones(JsonValue jsonVal){
		for (JsonValue zonesJson = jsonVal.getChild("zones"); zonesJson != null; zonesJson = zonesJson.next) {
			for (JsonValue zone = zonesJson.child; zone != null; zone = zone.next) {
				String zoneName = zone.name;
				zones.put(zoneName, new ArrayList<Station>());
				for (JsonValue stationName = zone.child; stationName != null; stationName = stationName.next) {
					Station station = getStationByName(stationName.asString());
					zones.get(zoneName).add(station);
				}
			}
		}
	}

	private void parseConnections(JsonValue jsonVal) {
		for (JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
			String stationName1 = "";
			String stationName2 = "";
			for (JsonValue val = connection.child; val != null; val = val.next) {
				if (val.name.equalsIgnoreCase("station1")) {
					stationName1 = val.asString();
				} else {
					stationName2 = val.asString();
				}
			}
            // Pre-defined connections are Gold.
            Station station1 = getStationByName(stationName1);
            Station station2 = getStationByName(stationName2);
			addConnection(new Connection(station1, station2, Connection.Material.GOLD));
		}

        parseInvalidConnection(jsonVal);
	}

	private void parseInvalidConnection(JsonValue jsonVal) {
        for (JsonValue connection = jsonVal.getChild("invalidConnections"); connection != null; connection = connection.next) {
            String stationName1 = "";
            String stationName2 = "";
            for (JsonValue val = connection.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("station1")) {
                    stationName1 = val.asString();
                } else {
                    stationName2 = val.asString();
                }
            }
            Set<String> stations = new HashSet<>();
            stations.add(stationName1);
            stations.add(stationName2);
            invalidConnections.add(stations);
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
                addJunction(new Junction(name, new Position(x, y)));
            else
                addStation(new Station(name, new Position(x, y)));
		}
	}

    public Connection getConnectionBetween(Station station1, Station station2) {
        for (Connection connection : connections) {
            if (connection.getStation1().equals(station1) && connection.getStation2().equals(station2)
                    || connection.getStation1().equals(station2) && connection.getStation2().equals(station1)) {
                return connection;
            }
        }
        return null;
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
        return getConnectionBetween(a, b) != null;
	}

    public Connection getConnectionBetween(Position a, Position b) {
        for (Connection connection : connections) {
            Position p1 = connection.getStation1().getLocation();
            Position p2 = connection.getStation2().getLocation();
            double dist1 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(),
                    p2.getY(), a.getX(), a.getY());
            double dist2 = Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(),
                    p2.getY(), b.getX(), b.getY());
            if (dist1 <= 0.0001 && dist2 <= 0.0001)
                return connection;
        }
        return null;
    }

    public boolean prospectiveConnectionIsValid(Connection prospectiveConnection) {
        Station station1 = prospectiveConnection.getStation1();
        Station station2 = prospectiveConnection.getStation2();
        if (doesConnectionExist(station1.getName(), station2.getName())) return false;
        if (doesProspectiveConnectionIntersectExisting(prospectiveConnection)) return false;
        for (Set<String> invalidConnection : invalidConnections) {
            if (invalidConnection.contains(station1.getName()) &&
                    invalidConnection.contains(station2.getName())) return false;
        }
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

	public void addStation(Station station) {
		stations.add(station);
	}

	public void addJunction(Junction junction) {
		stations.add(junction);
		junctions.add(junction);
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
		if (!EventReplayer.isReplaying() && !junctions.isEmpty() && random.nextDouble() <= JUNCTION_FAILURE_RATE) {
			Junction junction = junctions.get(random.nextInt(junctions.size()));
			breakJunction(junction);
			EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.BROKEN_JUNCTION, junction));
		} else {
			lastBroken = null;
		}
	}

	public void breakJunction(Junction junction) {
		junction.setFailureDuration(JUNCTION_FAILURE_DURATION);
		lastBroken = junction;
		EventReplayer.saveReplayEvent(new ReplayEvent(GameEvent.BROKEN_JUNCTION, junction));
	}

	public Junction getLastBroken() {
		return lastBroken;
	}

	public List<Station> getStations() {
		return stations;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void addConnection(Connection connection) {
		connections.add(connection);
        /* Don't want to keep recomputing distances whilst we are adding
        all the connections as the map is initialised */
        if (initialised) {
            distances.clear();
            computeDistances();
        }
	}

    public void removeConnection(Connection connection) {
        Player connectionOwner = connection.getOwner();
        if (connectionOwner != null) {
            connectionOwner.removeOwnedConnection(connection);
        }

		connections.remove(connection);

        distances.clear();
        computeDistances();
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
				Station stationi = stations.get(i);
				Station stationj = stations.get(j);
				if (i == j) {
					distances.get(i).add(0f);
				} else if (doesConnectionExist(stationi.getName(), stationj.getName())) {
					distances.get(i).add(Station.getDistance(stationi, stationj));
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
		String zoneA = getZone(a);
		String zoneB = getZone(b);

		if (areZonesConnected(zoneA, zoneB)) {
			return distances.get(stations.indexOf(a)).get(stations.indexOf(b));
		}

		float shortestCrossZoneDistance = Float.MAX_VALUE;
		Station closestStationA = null;
		Station closestStationB = null;
		for (Station stationZone1 : zones.get(zoneA)) {
			for (Station stationZone2 : zones.get(zoneB)) {
				float distance = Station.getDistance(stationZone1, stationZone2);
				if (distance < shortestCrossZoneDistance) {
					closestStationA = stationZone1;
					closestStationB = stationZone2;
					shortestCrossZoneDistance = distance;
				}
			}
		}

		return getShortestRouteDistance(a, closestStationA)
				+ shortestCrossZoneDistance
				+ getShortestRouteDistance(b, closestStationB);
	}

	public boolean areZonesConnected(String zone1, String zone2) {
		for (Station station1 : zones.get(zone1)) {
			for (Station station2 : zones.get(zone2)) {
				if (doesConnectionExist(station1.getName(), station2.getName())) return true;
			}
		}
		return false;
	}

	public String getZone(Station station) {
		for (java.util.Map.Entry<String, List<Station>> entry : zones.entrySet()) {
			String zone = entry.getKey();
			List<Station> stations = entry.getValue();
			if (stations.contains(station)) return zone;
		}
		return "";
	}

}
