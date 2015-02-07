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
	private ResourceManager resourceManager;
	
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
	public Goal generateEasyGoal(int turn) {
		Map map = Game.getInstance().getMap();

		Station origin;
		do {
			origin = map.getRandomStation();
		} while (origin instanceof CollisionStation);
		Station destination;
		do {
			destination = map.getRandomStation();
			// always true, really?
		} while ((destination == origin || destination instanceof CollisionStation) && (origin.getLocation().getX() - destination.getLocation().getX() > 50) && (origin.getLocation().getY() - destination.getLocation().getY() > 50));

		Goal goal = new Goal(origin, destination, turn);

		System.out.println("Easy goal");
		return goal;
	}

	public Goal generateMediumGoal(int turn) {
		Map map = Game.getInstance().getMap();

		Station origin;
		do {
			origin = map.getRandomStation();
		} while (origin instanceof CollisionStation);
		Station destination;
		do {
			destination = map.getRandomStation();
			// always true, really?
		} while ((destination == origin || destination instanceof CollisionStation) && (origin.getLocation().getX() - destination.getLocation().getX() > 50) && (origin.getLocation().getY() - destination.getLocation().getY() > 50));

		Goal goal = new Goal(origin, destination, turn);

        goal.addConstraint(resourceManager.getRandomTrain());

		System.out.println("Medium goal");
		return goal;

	}

	public Goal generateDifficultGoal(int turn) {
		Map map = Game.getInstance().getMap();
		Station via;
		Station origin;
		do {
			origin = map.getRandomStation();
		} while (origin instanceof CollisionStation);
		Station destination;
		do {
			destination = map.getRandomStation();
			// always true, really?
		} while ((destination == origin || destination instanceof CollisionStation) || (origin.getLocation().getX() - destination.getLocation().getX() > 50) || (origin.getLocation().getY() - destination.getLocation().getY() > 50));

		do {
			via = map.getRandomStation();
		} while ((via == origin) || (via == destination));
		Goal goal = new Goal(origin, destination, turn);

		goal.addConstraint(resourceManager.getRandomTrain());
		goal.addConstraint(via);

		System.out.println("Difficult goal");
		return goal;

	}

}
