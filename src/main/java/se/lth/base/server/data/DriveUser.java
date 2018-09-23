package se.lth.base.server.data;

/**
 * Data class for a drive user
 *
 * @author Group 1 ETSN05 2018
 * @see DriveUserDataAccess
 */
public class DriveUser {
	private final int driveId, userId;
	private final String start, stop;
	private final boolean driver, accepted;
	
	public DriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted) {
		this.driveId = driveId;
		this.userId = userId;
		this.start = start;
		this.stop = stop;
		this.driver = driver;
		this.accepted = accepted;
	}
	
	public int getDriveId() {
		return driveId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public String getStart() {
		return start;
	}
	
	public String getStop() {
		return stop;
	}
	
	public boolean isDriver() {
		return driver;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
}
