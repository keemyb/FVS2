package gameLogic.goal;

import Util.Vowel;
import gameLogic.map.Station;
import gameLogic.resource.Train;

public class Goal {
    private Station origin;
	private Station destination;
	private int turnIssued;
	private boolean complete = false;
	//constraints
	private Train requiredTrain = null;
	private Station via = null;

	public Goal(Station origin, Station destination, int turn) {
		this.origin = origin;
		this.destination = destination;
		this.turnIssued = turn;
	}
	
	public void addConstraint(Station via) {
        this.via = via;
	}

    public void addConstraint(Train train) {
        this.requiredTrain = train;
    }

	public boolean isComplete(Train train) {
        if (!train.historyContains(origin, turnIssued)) return false;
        if (!train.historyContains(destination, turnIssued)) return false;

        if (requiredTrain != null) {
            if (train != requiredTrain) return false;
        }

        if (via != null) {
            if (!train.historyContains(via, turnIssued)) return false;
        }

        return true;
	}

    public Station getDestination() {
        return destination;
    }

    public Station getOrigin() {
        return origin;
    }
	
	public String toString() {
		String trainString = "any train";
        if (requiredTrain != null) {
            String trainName = requiredTrain.getName();
            String article;
            if (Vowel.startsWithVowel(trainName)) {
                article = "an ";
            } else {
                article = "a ";
            }
            trainString = article + requiredTrain.getName();
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