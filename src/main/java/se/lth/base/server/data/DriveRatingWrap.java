package se.lth.base.server.data;

import java.util.List;

/**
 * Data class for a drive rating wrap
 *
 * @author Group 1 ETSN05 2018
 */

public class DriveRatingWrap {
    private final int userId;
    private final int driveId;
    private final List<DriveRating> ratings;

    /**
     * Holds the ratings from users from a drive.
     *
     * @param userId  the Id of the user.
     * @param driveId the Id of the drive.
     * @param ratings a list of drive ratings.
     */
    public DriveRatingWrap(int userId, int driveId, List<DriveRating> ratings) {
        this.userId = userId;
        this.driveId = driveId;
        this.ratings = ratings;
    }

    /** @return the Id of the user. */
    public int getUserId() {
        return userId;
    }

    /** @return the Id of the drive. */
    public int getDriveId() {
        return driveId;
    }

    /** @return a list of drive ratings. */
    public List<DriveRating> getRatings() {
        return ratings;
    }

}
