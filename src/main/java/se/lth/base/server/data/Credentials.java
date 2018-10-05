package se.lth.base.server.data;

import com.google.gson.annotations.Expose;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * Used for authentication and user operations requiring passwords.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class Credentials {

    // Password hashing function parameters.
    private static final int SIZE = 256;
    private static final int ITERATION_COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private final String username;
    @Expose(serialize = false)
    private final String password;
    private final Role role;
    private final User user;

    public Credentials(String username, String password, Role role, User user) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.user = user;
    }

    static long generateSalt() {
        return new SecureRandom().nextLong();
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }

    public void validate() {
        if (user == null) {
            throw new WebApplicationException("No user data", Response.Status.BAD_REQUEST);
        }

        String firstName = this.user.getFirstName();
        if (firstName == null || firstName.trim().length() < 2) {
            throw new WebApplicationException("First name too short", Response.Status.BAD_REQUEST);
        }

        String lastName = this.user.getFirstName();
        if (lastName == null || lastName.trim().length() < 2) {
            throw new WebApplicationException("Last name too short", Response.Status.BAD_REQUEST);
        }

        Date dateOfBirth = this.user.getDateOfBirth();
        if (dateOfBirth == null) {
            throw new WebApplicationException("No date of birth", Response.Status.BAD_REQUEST);
        }

        long currentAge = new Date().getTime() - dateOfBirth.getTime();
        long legalAge = 1000 * 60 * 60 * 24 * 365 * 18;
        if (currentAge < legalAge) {
            throw new WebApplicationException("User not over 18 years old", Response.Status.BAD_REQUEST);
        }

        if (this.user.getDrivingLicence() == null) {
            throw new WebApplicationException("Driving license not specified", Response.Status.BAD_REQUEST);
        }

        String email = this.user.getEmail();
        if (email != null) {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
            } catch (AddressException e) {
                throw new WebApplicationException("Email not valid", Response.Status.BAD_REQUEST);
            }

            String[] tokens = email.split("@");
            if (tokens.length != 2 ||
                    tokens[0] != null || tokens[0].trim().length() > 0 ||
                    tokens[1] != null || tokens[1].trim().length() > 0) {
                throw new WebApplicationException("Email not valid", Response.Status.BAD_REQUEST);
            }

        } else {
            throw new WebApplicationException("No email specified", Response.Status.BAD_REQUEST);
        }

        String password = this.password;
        if (password == null || password.length() < 8 || ) {
            throw new WebApplicationException("Password not secure enough", Response.Status.BAD_REQUEST);
        }
    }

    public boolean hasPassword() {
        return password != null;
    }

    /**
     * Hash password using hashing algorithm intended for this purpose.
     *
     * @return base64 encoded hash result.
     */
    UUID generatePasswordHash(long salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(),
                    ByteBuffer.allocate(8).putLong(salt).array(),
                    ITERATION_COST, SIZE);
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] blob = f.generateSecret(spec).getEncoded();
            LongBuffer lb = ByteBuffer.wrap(blob).asLongBuffer();
            return new UUID(lb.get(), lb.get());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }
}