package se.lth.base.server.data;

public class DriveRating {
	private final int ratedUserId;
	private final int rating;
	
	public DriveRating(int ratedUserId, int rating) {
		this.ratedUserId = ratedUserId;
		this.rating = rating;
	}

	int getRatedUserId() {
		return ratedUserId;
	}

	public int getRating() {
		return rating;
	}
}
