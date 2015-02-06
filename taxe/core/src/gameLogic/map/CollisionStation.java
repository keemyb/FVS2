package gameLogic.map;

public class CollisionStation extends Station {

	boolean broken;

	public CollisionStation(String name, IPositionable location) {
		super(name, location);
		broken = false;
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
}
