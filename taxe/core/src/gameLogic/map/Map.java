package gameLogic.map;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    /* The row represents the origin station
           col represents the destination station
           the List of stations is inclusive of the two endPoints.
     */
    private Table<Station, Station, List<Station>> shortestRoutes = HashBasedTable.create();

    /* Saving random instance so we don't have keep instantiating new
    ones every time we need a random number.
     */
    private Random random = new Random();

    public Map() {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();

        initialise();

        // Computing all possible routes for later use
        for (Station station1 : stations) {
            for (Station station2 : stations) {
                if (station1.equals(station2)) continue;
                if (shortestRoutes.contains(station2, station1)) continue;
                List<Station> route = getShortestRoute(station1, station2);
                // Place the reversed one in too to save time.
                shortestRoutes.put(station1, station2, route);
                shortestRoutes.put(station2, station1, reverseRoute(route));
            }
        }
    }

    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));

        parseStations(jsonVal);
        parseConnections(jsonVal);
    }

    private void parseConnections(JsonValue jsonVal) {
        for(JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";

            for(JsonValue val = connection.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }

            addConnection(station1, station2);
        }
    }

    private void parseStations(JsonValue jsonVal) {
        for(JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
            String name = "";
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            for(JsonValue val = station.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if(val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if(val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            if (isJunction) {
                addJunction(name, new Position(x,y));
            } else {
                addStation(name, new Position(x, y));
            }
        }
    }

    /**
     * A connection is considered to exist if there is a unbroken connection (track)
     * between two stations.
     * @param station1 A station to check the connection between.
     * @param station2 The other station to check the connection between.
     * @return true if a connection exists, false otherwise.
     */
    public boolean doesConnectionExist(String station1, String station2) {
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            if (s1.equals(station1) && s2.equals(station2)
                || s1.equals(station2) && s2.equals(station1)) {
                if (connection.isBroken()) {
                	return false;
                } else {
                	return true;
                }
            }
        }

        return false;
    }

    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }
    
    public Connection getRandomConnection() {
    	return connections.get(random.nextInt(connections.size()));
    }

    public Station addStation(String name, Position location) {
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }
    
    public CollisionStation addJunction(String name, Position location) {
    	CollisionStation newJunction = new CollisionStation(name, location);
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
        for(Connection connection : connections) {
            if(connection.getStation1() == station || connection.getStation2() == station) {
                results.add(connection);
            }
        }
        return results;
    }

    public Station getStationByName(String name) {
        int i = 0;
        while(i < stations.size()) {
            if(stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else{
                i++;
            }
        }
        return null;
    }

    public Station getStationFromPosition(IPositionable position) {
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }

        throw new RuntimeException("Station does not exist for that position");
    }

    public List<Station> createRoute(List<IPositionable> positions) {
        List<Station> route = new ArrayList<Station>();

        for (IPositionable position : positions) {
            route.add(getStationFromPosition(position));
        }

        return route;
    }

    /**
     * Returns the shortest route from one track to another.
     * @param from The station to start from.
     * @param to The station to go to.
     * @return a list of stations representing the shortest route between the two stations.
     */
    public List<Station> getShortestRoute(Station from, Station to) {
        if (shortestRoutes.contains(from, to)) return shortestRoutes.get(from, to);

        List<List<Station>> routes = new ArrayList<List<Station>>();
        List<Station> currentRoute = new ArrayList<Station>();
        currentRoute.add(from);
        getRoutes(to, currentRoute, new ArrayList<Station>(), routes);

        int shortestRouteLength = Integer.MAX_VALUE;
        List<Station> shortestRoute = null;
        for (List<Station> route : routes) {
            int routeLength = getRouteLength(route);
            if (routeLength < shortestRouteLength) {
                shortestRoute = route;
            }
        }

        return shortestRoute;
    }

    /**
     * Returns valid routes from one station to another.
     * @param destination   The finishing point of the route.
     * @param currentRoute  The route traversed so far.
     * @param visitedStations The tracks that have been visited so far.
     * @param routes        The list of valid routes found so far.
     * @return The list of the routes between the two points.
     */
    private void getRoutes(Station destination,
                                  List<Station> currentRoute,
                                  List<Station> visitedStations,
                                  List<List<Station>> routes) {
        Station lastStationInCurrentRoute = currentRoute.get(currentRoute.size() - 1);

        // If we have backtracked to the first station, and we have visited all its connected stations,
        // there are no more solutions.
        if (currentRoute.size() == 1 && visitedStations.containsAll(getConnectedStations(lastStationInCurrentRoute))) return;

        List<Station> connectedStations = getConnectedStations(lastStationInCurrentRoute);

        // Discard all visited stations
        connectedStations.removeAll(visitedStations);

        // Recurse over all non visited station
        for (Station nextStation : connectedStations){
            currentRoute.add(nextStation);
            // If the last station visited is our destination, add the route
            // and keep looking for more routes
            if (nextStation.equals(destination)) {
                // Cloning the route as we don't want it to be mutated.
                routes.add(new ArrayList<Station>(currentRoute));
                currentRoute.remove(destination);
            } else {
                visitedStations.add(nextStation);
                getRoutes(destination, currentRoute, visitedStations, routes);
                currentRoute.remove(nextStation);
            }
        }
    }

    /**
     * @param station the station to find neighbours of.
     * @return a list of stations connected to a station.
     */
    private List<Station> getConnectedStations(Station station) {
        List<Station> connectedStations = new ArrayList<Station>();
        for (Connection connection : getConnectionsFromStation(station)) {
            Station station1 = connection.getStation1();
            Station station2 = connection.getStation2();
            if (station1.equals(station)) {
                connectedStations.add(station2);
            } else connectedStations.add(station1);
        }
        return connectedStations;
    }

    /**
     * Returns the cumulative length of a route.
     * @param route the route that you want to compute the length of
     * @return the total length of the route.
     */
    public int getRouteLength(List<Station> route) {
        int length = 0;
        for (int i=0; i < route.size() - 1; i++) {
            Station currentStation = route.get(i);
            Station nextStation = route.get(i + 1);
            length += currentStation.getEuclideanDistance(nextStation);
        }
        return length;
    }

    /**
     * Reverses a route.
     * @param route the route that you want to be reversed.
     * @return the reversed route.
     */
    private List<Station> reverseRoute(List<Station> route) {
        List<Station> reversedRoute = new ArrayList<Station>();

        for (Station station : route) {
            reversedRoute.add(0, station);
        }
        return reversedRoute;
    }
}
