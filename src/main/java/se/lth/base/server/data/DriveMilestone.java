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
    
    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result + (int) (departureTime ^ (departureTime >>> 32));
    	result = prime * result + driveId;
    	result = prime * result + ((milestone == null) ? 0 : milestone.hashCode());
    	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	DriveMilestone other = (DriveMilestone) obj;
    	if (departureTime != other.departureTime)
    		return false;
    	if (driveId != other.driveId)
    		return false;
    	if (milestone == null) {
    		if (other.milestone != null)
    			return false;
    	} else if (!milestone.equals(other.milestone))
    		return false;
    	return true;
    }
}
