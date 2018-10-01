package se.lth.base.server.data;

import java.security.Principal;
import java.sql.Timestamp;

public class User implements Principal {

    public static User NONE = new User(0, Role.NONE, "-", "-", "-", "0", "-", 0, Timestamp.valueOf("2018-01-01 00:00:00"), false, 0, 0, "-");

    private final int user_id;
    private final Role role;
    private final String username;
    private final String first_name;
    private final String last_name;
    private final String phone_number;
    private final String email;
    private final int gender;
    private final Timestamp date_of_birth;
    private final Boolean driving_licence;
    private final int rating_total_score;
    private final int number_of_ratings;
    private final String warning;

    public User(int user_id, Role role, String username, String first_name, String last_name, String phone_number,
			String email, int gender, Timestamp date_of_birth, Boolean driving_licence, int rating_total_score,
			int number_of_ratings, String warning) {
		this.user_id = user_id;
		this.role = role;
		this.username = username;
		this.first_name = first_name;
		this.last_name = last_name;
		this.phone_number = phone_number;
		this.email = email;
		this.gender = gender;
		this.date_of_birth = date_of_birth;
		this.driving_licence = driving_licence;
		this.rating_total_score = rating_total_score;
		this.number_of_ratings = number_of_ratings;
		this.warning = warning;
	}

    public Role getRole() {
        return role;
    }

    public int getId() {
        return user_id;
    }

    @Override
    public String getName() {
        return username;
    }
	
	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public String getEmail() {
		return email;
	}

	public int getGender() {
		return gender;
	}

	public Timestamp getDate_of_birth() {
		return date_of_birth;
	}

	public Boolean getDriving_licence() {
		return driving_licence;
	}

	public int getRating_total_score() {
		return rating_total_score;
	}

	public int getNumber_of_ratings() {
		return number_of_ratings;
	}

	public String getWarning() {
		if (warning != null)
			return warning;
		return " ";
	}
}
