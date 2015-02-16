package gameLogic.resource;

import Util.Tuple;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

public class Train extends Resource {
    private String leftImage;
    private String rightImage;
    private IPositionable position;
    private TrainActor actor;
    private int speed;
    private String name;
    // Final destination should be set to null after firing the arrival event
    private Station finalDestination;

    // Should NOT contain current position!
    private List<Station> route;

    //Station name and turn number
    private List<Tuple<String, Integer>> history;


    public Train(String name, String leftImage, String rightImage, int speed) {
        this.name = name;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.speed = speed;
        history = new ArrayList<Tuple<String, Integer>>();
        route =  new ArrayList<Station>();
    }

    public Train(Train train) {
        this(train.name, train.leftImage, train.rightImage, train.speed);
    }
    
    public String getName() {
    	return name;
    }

    public String getLeftImage() {
        return "trains/" + leftImage;
    }

    public String getRightImage() {
        return "trains/" + rightImage;
    }

    public String getCursorImage() {
        return "trains/cursor/" + leftImage;
    }

    public void setPosition(IPositionable position) {
        this.position = position;
        changed();
    }

    public IPositionable getPosition() {
        return position;
    }

    public void setActor(TrainActor actor) {
        this.actor = actor;
    }

    public TrainActor getActor() {
        return actor;
    }

    public void setRoute(List<Station> route) {
        // Final destination should be set to null after firing the arrival event
        if (route != null && route.size() > 0) finalDestination = route.get(route.size() - 1);

        this.route = route;
    }

    public boolean isMoving() {
        return finalDestination != null;
    }

    public List<Station> getRoute() {
        return route;
    }

    public Station getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Station station) {
        finalDestination = station;
    }
    
    public int getSpeed() {
        return speed;
    }

    /**
     * This method returns the list of tuples representing where this train has travelled.
     * The first object in the tuple is the name of the station, and the second object
     * is the turn that this train travelled to that station.
     * @return The train's history.
     */
    public List<Tuple<String, Integer>> getHistory() {
        return history;
    }

    /**
     * This method finds whether a train has travelled to a certain station on/after
     * a specified turn.
     * @param station The station that you want to check
     * @param turn The minimum turn to consider
     * @return true if the station appears in the history, on or after the turn specified
     */
    public boolean historyContains(Station station, int turn) {
        for(Tuple<String, Integer> entry: history) {
            if(entry.getFirst().equals(station.getName()) && entry.getSecond() >= turn) {
                return true;
            }
        }
        return false;
    }

    // Most recent station added to history
    public String getLastStation() {
        int size = history.size();
        return history.get(size-1).getFirst();
    }

    // Second most recent station added to history
    public String getSecondLastStation() {
        int size = history.size();
        if (size < 2){
            return"";
        }
        return history.get(size-2).getFirst();
    }


    //Station name and turn number
    public void addHistory(String stationName, int turn) {
        history.add(new Tuple<String, Integer>(stationName, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }

    /**
     * Trains are considered equal if they have the same name or speed
     * Note that if you add power-ups that modify speed, you may want to
     * change this method to either not consider speed at all, or it's
     * original speed.
     * @param o the object to compare with this train.
     * @return true if the object is equal to this train, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Train train = (Train) o;

        if (speed != train.speed) return false;
        if (name != null ? !name.equals(train.name) : train.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = speed;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
