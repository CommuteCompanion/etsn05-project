package se.lth.base.server.data;

/**
 * Data class for a drive milestone
 *
 * @author Group 1 ETSN05 2018
 * @see DriveMilestoneDataAccess
 */
public class DriveMilestone {
	private final int milestoneId, driveId;
	private final String milestone;
	
	public DriveMilestone(int milestoneId, int driveId, String milestone) {
		this.milestoneId = milestoneId;
		this.driveId = driveId;
		this.milestone = milestone;
	}
	
	public int getMilestoneId() {
		return milestoneId;
	}
	
	public int getDriveId() {
		return driveId;
	}
	
	public String getMilestone() {
		return milestone;
	}
}
