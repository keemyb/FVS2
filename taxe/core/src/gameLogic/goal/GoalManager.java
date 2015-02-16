package gameLogic.goal;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.Random;

public class GoalManager {
	public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    // The max distance between the origin and destination.
    public final static int MAX_ORIGIN_DEST_DISTANCE = 300;
    // The max distance between the via and the origin, and the via and destination.
    public final static int MAX_VIA_DISTANCE = MAX_ORIGIN_DEST_DISTANCE * 2 / 3;
    // How much the search radius will increase on failure
	public final static int SEARCH_RADIUS_INCREASE_STEP = MAX_ORIGIN_DEST_DISTANCE / 10;
	private ResourceManager resourceManager;
	
	public GoalManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	private Goal generateRandom(int turn) {
		Random random = new Random();
		float randomGoalDifficulty = random.nextFloat();

        Goal goal;

		if (randomGoalDifficulty >= 0.0) {
            goal = generateDifficultGoal(turn);
        } else if (randomGoalDifficulty >= 0.5) {
            goal = generateMediumGoal(turn);
        } else {
            goal = generateEasyGoal(turn);
        }

		return goal;
	}
	
	public void addRandomGoalToPlayer(Player player) {
		player.addGoal(generateRandom(player.getPlayerManager().getTurnNumber()));
	}

	public ArrayList<String> trainArrived(Train train, Player player) {
		ArrayList<String> completedString = new ArrayList<String>();
		for(Goal goal:player.getGoals()) {
			if(goal.isComplete(train)) {
				player.completeGoal(goal);
				player.removeResource(train);
				completedString.add("Player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
			}
		}
		System.out.println("Train arrived to final destination: " + train.getFinalDestination().getName());
		return completedString;
	}

    /**
     * This generates a simple A to B goal.
     * @param turn The turn that this goal will be issued
     * @return an easy goal.
     */
	public Goal generateEasyGoal(int turn) {
		Map map = Game.getInstance().getMap();

		Station origin;
		do {
			origin = map.getRandomStation();
		} while (origin instanceof CollisionStation);

		Station destination;
        int searchDistance = MAX_ORIGIN_DEST_DISTANCE - SEARCH_RADIUS_INCREASE_STEP;
        /* Here we increase the search distance at every iteration.
        This is so that we do not loop infinitely if there is no station in our
        maximum range specified.
        */
		do {
			destination = map.getRandomStation();
            searchDistance += SEARCH_RADIUS_INCREASE_STEP;
		} while ((destination == origin || destination instanceof CollisionStation)
                || (origin.getEuclideanDistance(destination) > searchDistance));
        /* ^^ Picking random stations until it is different from our destination,
        not a CollisionStation (junction) and within our search distance.
         */

        return new Goal(origin, destination, turn);
	}


    /**
     * This generates a medium goal from A to B, with a particular train.
     * @param turn The turn that this goal will be issued
     * @return a medium difficulty goal.
     */
	public Goal generateMediumGoal(int turn) {
        // Using an easy goal, then adding a train constraint.
        Goal easyGoal = generateEasyGoal(turn);

        easyGoal.addConstraint(resourceManager.getRandomTrain());

		return easyGoal;
	}

    /**
     * This generates a hard goal from A to B via C, with a particular train.
     * @param turn The turn that this goal will be issued
     * @return a difficult goal.
     */
	public Goal generateDifficultGoal(int turn) {
        // Using a medium goal, then adding a via constraint.
        Goal mediumGoal = generateMediumGoal(turn);

        Station destination = mediumGoal.getDestination();
        Station origin = mediumGoal.getOrigin();

        Map map = Game.getInstance().getMap();
        Station via;

        /* Here we increase the search distance at every iteration.
        This is so that we do not loop infinitely if there is no station in our
        maximum range specified.
        */
        int searchDistance = MAX_VIA_DISTANCE - SEARCH_RADIUS_INCREASE_STEP;
        do {
			via = map.getRandomStation();
            searchDistance += SEARCH_RADIUS_INCREASE_STEP;
        } while ((via == origin) || (via == destination)
                || (origin.getEuclideanDistance(via) > searchDistance || destination.getEuclideanDistance(via) > searchDistance));
        /* ^^ Picking random stations until it is different from our destination and origin,
        not a CollisionStation (junction) and within our search distance.
         */

		mediumGoal.addConstraint(via);

		return mediumGoal;
	}

}
