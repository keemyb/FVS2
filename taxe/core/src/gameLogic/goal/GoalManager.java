package gameLogic.goal;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoalManager {
	public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    // A generated route must have at-least this number of stations in it. (inclusive of origin and destination).
    public final static int MINIMUM_AMOUNT_OF_STATIONS_IN_SHORTEST_ROUTE = 4;
    private ResourceManager resourceManager;

    private Random random = new Random();
	
	public GoalManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	private Goal generateRandom(int turn) {
		Random random = new Random();
		float randomGoalDifficulty = random.nextFloat();

        Goal goal;

		if (randomGoalDifficulty >= 0.8) {
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

        List<Station> shortestRoute;
        do {
            destination = map.getRandomStation();
            shortestRoute = map.getShortestRoute(origin, destination);
        } while ((destination == origin || destination instanceof CollisionStation)
                || shortestRoute.size() < MINIMUM_AMOUNT_OF_STATIONS_IN_SHORTEST_ROUTE);

        return new Goal(origin, destination, turn, map.getRouteLength(shortestRoute));
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
        Map map = Game.getInstance().getMap();

        // Using a medium goal, then adding a via constraint.
        // We need a goal that has at-least one station between them
        /* Note this check isn't strictly necessary unless the
        MINIMUM_AMOUNT_OF_STATIONS_IN_SHORTEST_ROUTE is set <= 2 for some reason
        */
        Goal mediumGoal;
        List<Station> shortestRoute;
        do {
            mediumGoal = generateMediumGoal(turn);
            shortestRoute = map.getShortestRoute(mediumGoal.getOrigin(), mediumGoal.getDestination());
        } while (shortestRoute.size() < 2);

        int index = 1 + random.nextInt(shortestRoute.size() - 1);
        Station via = shortestRoute.get(index);

		mediumGoal.addConstraint(via);

		return mediumGoal;
	}

}
