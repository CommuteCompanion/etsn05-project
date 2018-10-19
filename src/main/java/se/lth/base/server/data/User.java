package se.lth.base.server.data;

import java.security.Principal;
import java.sql.Date;

public class User implements Principal {

    public static User NONE = new User(0, Role.NONE, "none@commutecompanion.se", "-", "-", "0", 0, Date.valueOf("2018-01-01").getTime(), false, 0, 0, 0);

    private final int userId;
    private final Role role;
    private final String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private final int gender;
    private final long dateOfBirth;
    private final Boolean drivingLicense;
    private final int ratingTotalScore;
    private final int numberOfRatings;
    private final int warning;

    public User(int userId, Role role, String email, String firstName, String lastName, String phoneNumber,
			int gender, long dateOfBirth, Boolean drivingLicense, int ratingTotalScore,
			int numberOfRatings, int warning) {
		this.userId = userId;
		this.role = role;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.drivingLicense = drivingLicense;
		this.ratingTotalScore = ratingTotalScore;
		this.numberOfRatings = numberOfRatings;
		this.warning = warning;
	}

    public Role getRole() {
        return role;
    }

    public int getId() {
        return userId;
    }

    /**
     * This is only a shortcut to satisfy Principal.
     * Will return a user's email; for clarity, use getEmail() instead.
     */
    @Override
    public String getName() {
		return email;
    }

    public String getEmail() {
        return email;
    }
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

	public String getLastName() {
		return lastName;
	}

	void setLastName(String lastName) {
        this.lastName = lastName;
    }

	public String getPhoneNumber() {
		return phoneNumber;
	}

	void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public int getGender() {
		return gender;
	}

	long getDateOfBirth() {
		return dateOfBirth;
	}

	boolean getDrivingLicense() {
		return drivingLicense;
	}

	int getRatingTotalScore() {
		return ratingTotalScore;
	}

	public int getNumberOfRatings() { return numberOfRatings; }

	public int getWarning() {
		return warning;
	}
}
