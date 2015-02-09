package gameLogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gameLogic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResourceManager {
    private static Random random = new Random();

	public static final int CONFIG_MAX_RESOURCES = 7;
	private static final String DEFAULT_TRAIN_NAME = "NO NAME";
	private static final int DEFAULT_TRAIN_SPEED = 50;

	private static List<Train> trains = new ArrayList<Train>();

	static {
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonVal = jsonReader.parse(Gdx.files.local("trains.json"));

		for(JsonValue train = jsonVal.getChild("trains"); train != null; train = train.next()) {
			String name = DEFAULT_TRAIN_NAME;
			int speed = DEFAULT_TRAIN_SPEED;
			for(JsonValue val  = train.child; val != null; val = val.next()) {
				if(val.name.equalsIgnoreCase("name")) {
					name = val.asString();
				} else {
					speed = val.asInt();
				}
			}
			String leftImage = getLeftTrainImage(name);
			String rightImage = getRightTrainImage(name);
			Train newTrain = new Train(name, leftImage, rightImage, speed);
			trains.add(newTrain);
		}
	}

	public static String getLeftTrainImage(String trainName) {
		return trainName.replaceAll(" ", "") + ".png";
	}

	public static String getRightTrainImage(String trainName) {
		return trainName.replaceAll(" ", "") + "Right.png";
	}

    public static Train getRandomTrain() {
    	int index = random.nextInt(trains.size());
		Train newTrain = new Train(trains.get(index));
		return newTrain;
    }

    public static void addRandomResourceToPlayer(Player player) {
        addResourceToPlayer(player, getRandomTrain());
    }

    private static void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() >= CONFIG_MAX_RESOURCES) {
			return;
        }

        resource.setPlayer(player);
        player.addResource(resource);
    }
}