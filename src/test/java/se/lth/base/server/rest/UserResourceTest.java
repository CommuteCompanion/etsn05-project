package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.data.*;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UserResourceTest extends BaseResourceTest {

    private static final GenericType<List<User>> USER_LIST = new GenericType<List<User>>() {
    };

    @Test
    public void notAuthenticatedCurrentUser() {
        User user = target("user").request().get(User.class);
        assertEquals(Role.NONE, user.getRole());
    }

    @Test(expected = ForbiddenException.class)
    public void notAuthenticatedGetAllUsers() {
        target("user").path("all").request().get(USER_LIST);
    }

    @Test
    public void loginCookies() {
        Response response = target("user")
                .path("login")
                .request()
                .post(Entity.json(TEST_CREDENTIALS));
        Cookie responseCookie = response
                .getCookies()
                .get(UserResource.USER_TOKEN);
        assertEquals("localhost", responseCookie.getDomain());
        assertEquals(UserResource.USER_TOKEN, responseCookie.getName());
        assertEquals("/rest", responseCookie.getPath());

        User userWithNoCookie = target("user").request()
                .get(User.class);
        assertEquals(Role.NONE, userWithNoCookie.getRole());

        User userWithCookie = target("user").request()
                .cookie(responseCookie)
                .get(User.class);
        assertEquals(Role.USER, userWithCookie.getRole());
    }

    @Test
    public void loginRememberMeCookie() {
        Response loginWithRememberMe = target("user")
                .path("login")
                .queryParam("remember", "true")
                .request()
                .post(Entity.json(TEST_CREDENTIALS));
        int maxAge = loginWithRememberMe.getCookies().get(UserResource.USER_TOKEN).getMaxAge();
        assertTrue(maxAge > 0);

        Response loginWithoutRememberMe = target("user")
                .path("login")
                .request()
                .post(Entity.json(TEST_CREDENTIALS));
        int noMaxAge = loginWithoutRememberMe.getCookies().get(UserResource.USER_TOKEN).getMaxAge();
        assertEquals(-1, noMaxAge);
    }

    @Test
    public void logout() {
        Response noSessionLogout = target("user")
                .path("logout")
                .request()
                .post(Entity.json(""));
        assertEquals("", noSessionLogout.getCookies().get(UserResource.USER_TOKEN).getValue());

        Response loginResponse = target("user")
                .path("login")
                .request()
                .post(Entity.json(ADMIN_CREDENTIALS));
        assertFalse(loginResponse.getCookies().get(UserResource.USER_TOKEN).getValue().isEmpty());

        Response logoutResponse = target("user")
                .path("logout")
                .request()
                .cookie(loginResponse.getCookies().get(UserResource.USER_TOKEN))
                .post(Entity.json(""));
        assertTrue(logoutResponse.getCookies().get(UserResource.USER_TOKEN).getValue().isEmpty());

        User currentUserAfterLogout = target("user")
                .request()
                .get(User.class);
        assertEquals(Role.NONE, currentUserAfterLogout.getRole());
    }

    @Test(expected = ForbiddenException.class)
    public void getAllUsersAsUser() {
        login(TEST_CREDENTIALS);
        target("user")
                .path("all")
                .request()
                .get(USER_LIST);
    }

    @Test
    public void testCreateUser() {
        Credentials newCredentials = new Credentials("pelle@commutecompanion.se", "passphrase123", Role.USER, TEST);
        User newUser = target("user")
                .request()
                .post(Entity.json(newCredentials), User.class);
        assertEquals(newCredentials.getEmail(), newUser.getEmail());
        assertEquals(newCredentials.getRole(), newUser.getRole());
        assertTrue(newUser.getId() > 0);

        // Test if we can login as new user
        login(newCredentials);
        User currentUser = target("user").request().get(User.class);
        assertEquals(newUser.getId(), currentUser.getId());
    }

    @Test(expected = ForbiddenException.class)
    public void deleteAdminAsUser() {
        login(TEST_CREDENTIALS);
        target("user")
                .path(Integer.toString(ADMIN.getId()))
                .request()
                .delete(Void.class); // Include response type to trigger exception
    }

    @Test(expected = ForbiddenException.class)
    public void deleteUserAsUser() {
        login(ADMIN_CREDENTIALS);
        Credentials newCredentials = new Credentials("pelle@commutecompanion.se", "passphrase123", Role.USER, TEST);
        User newUser = target("user")
                .request()
                .post(Entity.json(newCredentials), User.class);
        logout();
        login(TEST_CREDENTIALS);
        target("user")
                .path(Integer.toString(newUser.getId()))
                .request()
                .delete(Void.class); // Include response type to trigger exception
    }

    @Test
    public void deletingUserDeletesItsDrives() {
        GenericType<List<DriveWrap>> DRIVEWRAP_LIST = new GenericType<List<DriveWrap>>() {
        };

        //new user
        Credentials newCredentials = new Credentials("pelle@commutecompanion.se", "passphrase123", Role.USER, TEST);
        User newUser = target("user")
                .request()
                .post(Entity.json(newCredentials), User.class);
        login(newCredentials);

        //Add a drive as a user
        long departureTime = Timestamp.valueOf("2018-12-02 20:00:00").getTime();
        long arrivalTime = Timestamp.valueOf("2018-12-02 21:00:00").getTime();
        Drive drive = new Drive(-1, "A", "B", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 2, 1, true, true, false);
        DriveWrap driveWrap = new DriveWrap(drive, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        target("drive")
                .request()
                .post(Entity.json(driveWrap), DriveWrap.class);

        logout();
        login(ADMIN_CREDENTIALS);

        //Add a drive as admin
        departureTime = Timestamp.valueOf("2018-12-02 20:00:00").getTime();
        arrivalTime = Timestamp.valueOf("2018-12-02 21:00:00").getTime();
        drive = new Drive(-1, "A", "B", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 2, 1, true, true, false);
        driveWrap = new DriveWrap(drive, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        target("drive")
                .request()
                .post(Entity.json(driveWrap), DriveWrap.class);

        //get drives and expect there to be 2
        List<DriveWrap> alldrives = target("drive")
                .path("all")
                .request()
                .get(DRIVEWRAP_LIST);
        assertEquals(2, alldrives.size());

        //delete user
        target("user")
                .path(Integer.toString(newUser.getId()))
                .request()
                .delete(Void.class);

        // get drives and expect only 1.
        alldrives = target("drive")
                .path("all")
                .request()
                .get(DRIVEWRAP_LIST);
        assertEquals(1, alldrives.size());
    }

    @Test(expected = NotFoundException.class)
    public void deleteYourself() {
        login(TEST_CREDENTIALS);
        target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .delete(Void.class);
        logout();
        login(ADMIN_CREDENTIALS);
        target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .get(User.class);
    }

    @Test
    public void getAllUsers() {
        login(ADMIN_CREDENTIALS);
        List<User> users = target("user")
                .path("all")
                .request()
                .get(USER_LIST);
        assertTrue(users.stream().mapToInt(User::getId).anyMatch(id -> id == ADMIN.getId()));
        assertTrue(users.stream().mapToInt(User::getId).anyMatch(id -> id == TEST.getId()));
    }

    @Test
    public void getRoles() {
        login(ADMIN_CREDENTIALS);
        Set<Role> roles = target("user")
                .path("roles")
                .request()
                .get(new GenericType<Set<Role>>() {
                });
        assertEquals(Role.ALL_ROLES, roles);
    }

    @Test
    public void getUser() {
        login(ADMIN_CREDENTIALS);
        User responseTest = target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .get(User.class);
        assertEquals(TEST.getId(), responseTest.getId());
        assertEquals(TEST.getEmail(), responseTest.getEmail());
        assertEquals(TEST.getRole(), responseTest.getRole());
    }


    @Test(expected = NotFoundException.class)
    public void deleteTestUser() {
        login(ADMIN_CREDENTIALS);
        target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .delete(Void.class);
        target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .get(User.class);
    }

    @Test(expected = NotFoundException.class)
    public void deleteMissing() {
        login(ADMIN_CREDENTIALS);
        target("user")
                .path(Integer.toString(-1))
                .request()
                .delete(Void.class);
    }

    @Test(expected = NotFoundException.class)
    public void updateMissing() {
        Credentials TEST_CREDENTIALS_2 = new Credentials("commutecompaniontest@gmail.com", "password123", Role.USER, TEST);
        login(ADMIN_CREDENTIALS);
        target("user")
                .path(Integer.toString(-1))
                .request()
                .put(Entity.json(TEST_CREDENTIALS_2), User.class);
    }

    @Test(expected = WebApplicationException.class)
    public void dontDemoteYourself() {
        login(ADMIN_CREDENTIALS);
        Credentials update = new Credentials("admin", "password", Role.USER, ADMIN);
        target("user")
                .path(Integer.toString(ADMIN.getId()))
                .request()
                .put(Entity.json(update), User.class);
    }

    @Test
    public void updateUser() {
        login(ADMIN_CREDENTIALS);
        Credentials newTest = new Credentials("test2@commutecompanion.se", null, Role.ADMIN, TEST);
        User user = target("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .put(Entity.json(newTest), User.class);
        assertEquals(TEST.getId(), user.getId());
        assertEquals(newTest.getEmail(), user.getEmail());
        assertEquals(newTest.getRole(), user.getRole());
    }
}
