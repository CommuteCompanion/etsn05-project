package se.lth.base.server.data;

import java.util.List;

public class DriveRatingWrap {
	private final int userId;
	private final int driveId;
	private final List<DriveRating> ratings;
	
	public DriveRatingWrap(int userId, int driveId, List<DriveRating> ratings) {
				this.userId = userId;
				this.driveId = driveId;
				this.ratings = ratings;
	}

	public int getUserId() {
		return userId;
	}

	public int getDriveId() {
		return driveId;
	}

	public List<DriveRating> getRatings() {
		return ratings;
	}

}
