package se.lth.base.server.data;

public class Milestone {
	private final int driveId;
	private final String start;
	private final String stop;
	
	public Milestone(int driveId, String start, String stop) {
		this.driveId = driveId;
		this.start = start;
		this.stop = stop;
	}
	
	public int getDriveId() {
		return driveId;
	}
	
	public String getStart() {
		return start;
	}
	
	public String getStop() {
		return stop;
	}
}
