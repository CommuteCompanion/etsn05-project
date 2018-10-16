package se.lth.base.server.data;

import java.sql.Timestamp;

/**
 * Data class for a drive milestone
 *
 * @author Group 1 ETSN05 2018 
 * @see DriveMilestoneDataAccess
 */
public class DriveMilestone {
	private final int milestoneId;
    private final int driveId;
	private final String milestone;
	private final long departureTime;

	public DriveMilestone(int milestoneId, int driveId, String milestone, long departureTime) {
		this.milestoneId = milestoneId;
		this.driveId = driveId;
		this.milestone = milestone;
		this.departureTime = departureTime;
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

	public long getDepartureTime() {
		return departureTime;
	}
}
