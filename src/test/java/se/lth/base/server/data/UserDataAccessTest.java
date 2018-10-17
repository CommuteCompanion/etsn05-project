package se.lth.base.server.data;

import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;
import se.lth.base.server.database.DataAccessException;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class UserDataAccessTest extends BaseDataAccessTest {

    private final UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

    @Test
    public void addNewUser() {
        userDao.addUser(new Credentials("Generic", "qwerty", Role.USER, User.NONE));
        List<User> users = userDao.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("Generic") && u.getRole().equals(Role.USER)));
    }

    @Test(expected = DataAccessException.class)
    public void addDuplicatedUser() {
        userDao.addUser(new Credentials("Gandalf", "mellon", Role.USER, User.NONE));
        userDao.addUser(new Credentials("Gandalf", "vapenation", Role.USER, User.NONE));
    }

    @Test
    public void getUsersContainsAdmin() {
        assertTrue(userDao.getUsers().stream().anyMatch(u -> u.getRole().equals(Role.ADMIN)));
    }

    @Test
    public void removeNoUser() {
        assertFalse(userDao.deleteUser(-1));
    }

    @Test
    public void removeUser() {
        User user = userDao.addUser(new Credentials("Sven", "a", Role.ADMIN, User.NONE));
        assertTrue(userDao.getUsers().stream().anyMatch(u -> u.getEmail().equals("Sven")));
        userDao.deleteUser(user.getId());
        assertTrue(userDao.getUsers().stream().noneMatch(u -> u.getEmail().equals("Sven")));
    }

    @Test(expected = DataAccessException.class)
    public void authenticateNoUser() {
        userDao.authenticate(new Credentials("Waldo", "?", Role.NONE, User.NONE));
    }

    @Test
    public void authenticateNewUser() {
        userDao.addUser(new Credentials("Pelle", "!2", Role.USER, User.NONE));
        Session pellesSession = userDao.authenticate(new Credentials("Pelle", "!2", Role.NONE, User.NONE));
        assertEquals("Pelle", pellesSession.getUser().getEmail());
        assertNotNull(pellesSession.getSessionId());
    }

    @Test
    public void authenticateNewUserTwice() {
        userDao.addUser(new Credentials("Elin", "password", Role.USER, User.NONE));

        Session authenticated = userDao.authenticate(new Credentials("Elin", "password", Role.NONE, User.NONE));
        assertNotNull(authenticated);
        assertEquals("Elin", authenticated.getUser().getEmail());

        Session authenticatedAgain = userDao.authenticate(new Credentials("Elin", "password", Role.NONE, User.NONE));
        assertNotEquals(authenticated.getSessionId(), authenticatedAgain.getSessionId());
    }

    @Test
    public void removeNoSession() {
        assertFalse(userDao.removeSession(UUID.randomUUID()));
    }

    @Test
    public void removeSession() {
        userDao.addUser(new Credentials("MormorElsa", "kanelbulle", Role.USER, User.NONE));
        Session session = userDao.authenticate(new Credentials("MormorElsa", "kanelbulle", Role.NONE, User.NONE));
        assertTrue(userDao.removeSession(session.getSessionId()));
        assertFalse(userDao.removeSession(session.getSessionId()));
    }

    @Test(expected = DataAccessException.class)
    public void failedAuthenticate() {
        userDao.addUser(new Credentials("steffe", "kittylover1996!", Role.USER, User.NONE));
        userDao.authenticate(new Credentials("steffe", "cantrememberwhatitwas! nooo!", Role.NONE, User.NONE));
    }

    @Test
    public void checkSession() {
        userDao.addUser(new Credentials("uffe", "genius programmer", Role.ADMIN, User.NONE));
        Session session = userDao.authenticate(new Credentials("uffe", "genius programmer", Role.NONE, User.NONE));
        Session checked = userDao.getSession(session.getSessionId());
        assertEquals("uffe", checked.getUser().getEmail());
        assertEquals(session.getSessionId(), checked.getSessionId());
    }

    @Test
    public void checkRemovedSession() {
        userDao.addUser(new Credentials("lisa", "y", Role.ADMIN, User.NONE));
        Session session = userDao.authenticate(new Credentials("lisa", "y", Role.NONE, User.NONE));
        Session checked = userDao.getSession(session.getSessionId());
        assertEquals(session.getSessionId(), checked.getSessionId());
        userDao.removeSession(checked.getSessionId());
        try {
            userDao.getSession(checked.getSessionId());
            fail("Should not validate removed session");
        } catch (DataAccessException ignored) {
        }
    }

    @Test
    public void updateUserRating() {
    	DriveRating rating = new DriveRating(TEST.getId(), 1);
        userDao.updateUserRating(rating);
        assertEquals(1, userDao.getUser(TEST.getId()).getRatingTotalScore());
        rating = new DriveRating(TEST.getId(), 5);
        userDao.updateUserRating(rating);
        assertEquals(6, userDao.getUser(TEST.getId()).getRatingTotalScore());
    }

    @Test
    public void getUser() {
        User user = userDao.getUser(1);
        assertEquals(1, user.getId());
    }

    @Test(expected = DataAccessException.class)
    public void getMissingUser() {
        userDao.getUser(-1);
    }

    @Test(expected = DataAccessException.class)
    public void updateMissingUser() {
        userDao.updateUser(10, new Credentials("admin@lu.se", "password", Role.ADMIN, User.NONE));
    }

    @Test
    public void updateUser() {
        User user = userDao.updateUser(2, new Credentials("test2@lu.se", "newpass", Role.USER, User.NONE));
        assertEquals(2, user.getId());
        assertEquals("test2@lu.se", user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    public void updateWithoutPassword() {
        Session session1 = userDao.authenticate(new Credentials("test@lu.se", "password", Role.USER, User.NONE));
        userDao.updateUser(2, new Credentials("test2@lu.se", null, Role.USER, User.NONE));
        Session session2 = userDao.authenticate(new Credentials("test2@lu.se", "password", Role.USER, User.NONE));
        System.out.println(session1);
        System.out.println(session2);
    }
}
