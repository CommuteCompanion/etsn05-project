package se.lth.base.server.data;

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

    /**
     * Creates a DriveMilestone object to keep track of Milestones in a Drive.
     *
     * @param milestoneId   the Id of the Milestone.
     * @param driveId       the Id of the Drive.
     * @param milestone     the name of the Milestone.
     * @param departureTime the time of the departure from the MileStone.
     */
    public DriveMilestone(int milestoneId, int driveId, String milestone, long departureTime) {
        this.milestoneId = milestoneId;
        this.driveId = driveId;
        this.milestone = milestone;
        this.departureTime = departureTime;
    }

    /** @return the id of the Milestone. */
    public int getMilestoneId() {
        return milestoneId;
    }

    /** @return the id of the Drive. */
    public int getDriveId() {
        return driveId;
    }

    /** @return the name of the Milestone. */
    public String getMilestone() {
        return milestone;
    }

    /** @return the time of the departure from the MileStone*/
    public long getDepartureTime() {
        return departureTime;
    }
}
