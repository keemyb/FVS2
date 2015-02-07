package gameLogic.goal;

import Util.Tuple;
import gameLogic.map.Station;
import gameLogic.resource.Train;

public class Goal {
	private Station origin;
	private Station destination;
	private int turnIssued;
	private boolean complete = false;
	//constraints
	private Train train = null;
	private Station via = null;
	private String goalDifficulty;
	
	public Goal(Station origin, Station destination, int turn) {
		this.origin = origin;
		this.destination = destination;
		this.turnIssued = turn;
	}
	
	public void addConstraint(Station via) {
        this.via = via;
	}

    public void addConstraint(Train train) {
        this.train = train;
    }

	public boolean isComplete(Train train) {
		boolean passedOrigin = false;
		for(Tuple<String, Integer> history: train.getHistory()) {
			if(history.getFirst().equals(origin.getName()) && history.getSecond() >= turnIssued) {
				passedOrigin = true;
			}
		}
		if(train.getFinalDestination() == destination && passedOrigin) {
			if(this.train == null || this.train.equals(train.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public String toString() {
		String trainString = "any train";
        if (train != null) {
            trainString = "a " + train.getName();
        }

        String viaString = "";
        if (via != null) {
            viaString = " via " + via.getName();
        }

		return "Send " + trainString + " from " + origin.getName() + " to " + destination.getName() + viaString;

	}

	public void setComplete() {
		complete = true;
	}

	public boolean getComplete() {
		return complete;
	}
}