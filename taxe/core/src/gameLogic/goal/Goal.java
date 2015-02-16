package gameLogic.goal;

import Util.Vowel;
import gameLogic.map.Station;
import gameLogic.resource.Train;

public class Goal {
    private Station origin;
	private Station destination;
	private int turnIssued;
	private boolean complete = false;

    // These are additional constraints that may be null (for an easy or medium goal)
    // The train that must be used to complete this goal.
	private Train requiredTrain = null;
    // The station a train must pass to complete this goal.
	private Station via = null;
 
    private int score;
    // The value that a score will be scaled by every-time a new constraint is added.
    private static final float CONSTRAINT_SCORE_MODIFIER = 1.5F;

	public Goal(Station origin, Station destination, int turn) {
		this.origin = origin;
		this.destination = destination;
		this.turnIssued = turn;
        // The base score is the straight line distance between the origin and destination.
        this.score = origin.getEuclideanDistance(destination);
	}
	
	public void addConstraint(Station via) {
        this.via = via;
        score = origin.getEuclideanDistance(via);
        score += via.getEuclideanDistance(destination);
        score *= CONSTRAINT_SCORE_MODIFIER * CONSTRAINT_SCORE_MODIFIER;
	}

    public void addConstraint(Train train) {
        this.requiredTrain = train;
        score *= CONSTRAINT_SCORE_MODIFIER;
    }

    /**
     * A goal is considered complete if a train has visited both the goal's origin
     * and destination, and it also has met all of it's specified constraints, if any.
     * If there is a "via" requirement, the train will be checked to see if it has visited
     * that via station.
     * If there is a requiredTrain requirement, the train will be checked to see if it is
     * equal to that train specified.
     * @param train The train that will be checked for goal completion.
     * @return true if the train has met all of it's requirement's, false otherwise.
     */
	public boolean isComplete(Train train) {
        if (!train.historyContains(origin, turnIssued)) return false;
        if (!train.historyContains(destination, turnIssued)) return false;

        if (requiredTrain != null) {
            if (!train.equals(requiredTrain)) return false;
        }

        if (via != null) {
            if (!train.historyContains(via, turnIssued)) return false;
        }

        return true;
	}

    public int getScore() {
        return score;
    }

    public Station getDestination() {
        return destination;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getVia() {
        return via;
    }

    public Train getRequiredTrain() {
        return requiredTrain;
    }
	
	public String toString() {
		String trainString = "any train";
        if (requiredTrain != null) {
            String trainName = requiredTrain.getName();
            String article;
            // Pedantic vowel check so we use the right article.
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