package se.lth.base.server.data;

public class Milestone {
	private final int milestoneId, driveId;
	private final String milestone;
	
	public Milestone(int milestoneId, int driveId, String milestone) {
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
