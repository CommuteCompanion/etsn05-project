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
	private final boolean driver, accepted, rated;

    /**
     * A DriveUser is a User in a specific drive.
     *
     * @param driveId  the Id of the drive.
     * @param userId   the Id of the user.
     * @param start    the start location.
     * @param stop     the stop location.
     * @param driver   is the user a driver or not.
     * @param accepted is the user accepted to the drive or not.
     * @param rated    has the user rated the other user(s).
     */
	public DriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted, boolean rated) {
		this.driveId = driveId;
		this.userId = userId;
		this.start = start;
		this.stop = stop;
		this.driver = driver;
		this.accepted = accepted;
        this.rated = rated;
	}

	/** @return the Id of the drive. */
	public int getDriveId() {
        return driveId;
	}

	/** @return the Id of the user. */
    public int getUserId() {
        return userId;
	}

	/** @return the start position. */
	public String getStart() {
        return start;
	}

	/** @return the stop position. */
    public String getStop() {
        return stop;
    }

    /** @return true if the user is the driver, otherwise false. */
	public boolean isDriver() {
        return driver;
    }

    /** @return true if the user is the accepted to the drive, otherwise false. */
	public boolean isAccepted() {
        return accepted;
    }

    /** @return true if the user has rated the other user(s) in the drive, otherwise false. */
	public boolean hasRated() {
		return rated;
	}
}
