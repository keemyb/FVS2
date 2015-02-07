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
		int randomGoalDifficulty = random.nextInt(4 - 1) + 1;

		if (randomGoalDifficulty == 1) {
			Goal goal = generateEasyGoal(turn);
			return goal;
		}
		else if (randomGoalDifficulty == 2) {
			Goal goal = generateMediumGoal(turn);

		}

		else if (randomGoalDifficulty == 3){
			Goal goal = generateDifficultGoal(turn);
		}


			Map map = Game.getInstance().getMap();


			Station origin;
			do {
				origin = map.getRandomStation();
			} while (origin instanceof CollisionStation);
			Station destination;
			do {
				destination = map.getRandomStation();
				// always true, really?
			} while ((destination == origin || destination instanceof CollisionStation));

			Goal goal = new Goal(origin, destination, turn);

			// Goal with a specific train
			if (random.nextInt(2) == 1) {
				goal.addConstraint("train", resourceManager.getTrainNames().get(random.nextInt(resourceManager.getTrainNames().size())));
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

		// Goal with a specific train
		/*Random random = new Random();
		if (random.nextInt(2) == 1) {
			goal.addConstraint("train", resourceManager.getTrainNames().get(random.nextInt(resourceManager.getTrainNames().size())));
		} */

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

		// Goal with a specific train
		Random random = new Random();

		goal.addConstraint("train", resourceManager.getTrainNames().get(random.nextInt(resourceManager.getTrainNames().size())));


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

		// Goal with a specific train
		Random random = new Random();

		goal.addConstraint("train", resourceManager.getTrainNames().get(random.nextInt(resourceManager.getTrainNames().size())));
		goal.addConstraint("via", via.getName());

		System.out.println("Difficult goal");
		return goal;

	}

}
