package se.lth.base.server.data;

/**
 * Data class for a drive rating
 *
 * @author Group 1 ETSN05 2018
 */
public class DriveRating {
    private final int ratedUserId;
    private final int rating;

    /**
     *Updates the users rating after a drive.
     * @param ratedUserId the Id of the rated user.
     * @param rating the rating.
     */
    public DriveRating(int ratedUserId, int rating) {
        this.ratedUserId = ratedUserId;
        this.rating = rating;
    }

    /** @return the user id of the rated user. */
    int getRatedUserId() {
      return ratedUserId;
    }

    /** @return the rating*/
    public int getRating() {
      return rating;
    }
}
