package gameLogic.map;

import fvs.taxe.actor.StationActor;

public class Station{
	private String name;
	private IPositionable location;
	private StationActor actor;

	public Station(String name, IPositionable location) {
		this.name = name;
		this.location = location;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IPositionable getLocation() {
		return location;
	}

	public void setLocation(IPositionable location) {
		this.location = location;
	}

    public int getEuclideanDistance(Station station) {
        int x1 = location.getX();
        int y1 = location.getY();
        int x2 = station.location.getX();
        int y2 = station.location.getY();

        int xDelta = x1 - x2;
        int yDelta = y1 - y2;

        return (int) Math.sqrt(xDelta * xDelta + yDelta * yDelta);
    }
	
	public void setActor(StationActor actor){
		this.actor = actor;
	}
	
	public StationActor getActor(){
		return actor;
	}
	
}
