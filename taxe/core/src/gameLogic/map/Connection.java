package gameLogic.map;

public class Connection {
	private Station station1;
	private Station station2;
	public boolean broken;
	
	public Connection(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		this.broken = false;
	}
	
	public Station getStation1() {
		return this.station1;
	}

	public Station getStation2() {
		return this.station2;
	}
	
	public boolean isBroken() {
		return this.broken;
	}
	
	public void setBroken(boolean broken) {
		this.broken = broken;
	}
}
