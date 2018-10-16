package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;


public class DriveResourceTest extends BaseResourceTest {

	private static final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>() {
	};
	private static final GenericType<List<DriveWrap>> DRIVEWRAP_LIST = new GenericType<List<DriveWrap>>() {
	};


    private final int idStart = 3;
    private final User SEARCH_TEST_1 = new User(idStart, Role.USER, "test1@commutecompanion.se", "ST1FirstName", "ST1LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("test1@commutecompanion.se", "test", Role.USER, SEARCH_TEST_1);

    private final User SEARCH_TEST_2 = new User(idStart, Role.USER, "test2@commutecompanion.se", "ST2FirstName", "ST2LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_2 = new Credentials("test2@commutecompanion.se", "test", Role.USER, SEARCH_TEST_2);

    @Test(expected = ForbiddenException.class)
    public void getDrivesAsUser() {
        login(TEST_CREDENTIALS);
        target("drive")
                .path("all")
                .request()
                .get(DRIVE_LIST);
    }

    @Test
    public void getDrivesAsAdmin() {
        login(ADMIN_CREDENTIALS);
        List<Drive> drives = target("drive")
                .path("all")
                .request()
                .get(DRIVE_LIST);
        assertTrue(drives.isEmpty());
    }

    @Test
    public void getDrivesForUserAsRightUser() {
        login(TEST_CREDENTIALS);
        List<DriveWrap> drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVEWRAP_LIST);
        assertTrue(drives.isEmpty());

        //Add user to drive
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        Drive drive1 = new Drive(0, "A", "F", System.currentTimeMillis(), System.currentTimeMillis() + 1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        drive1 = driveDao.addDrive(drive1);
        int drive1Id = drive1.getDriveId();
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, TEST.getId(), "A", "B", false, false, false);

        drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVEWRAP_LIST);
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDrive().getDriveId(), drive1Id);
    }

    @Test(expected = WebApplicationException.class)
    public void getDrivesForUserAsWrongUser() {
        login(TEST_CREDENTIALS);
        List<DriveWrap> drives = target("drive")
                .path("user/" + ADMIN.getId())
                .request()
                .get(DRIVEWRAP_LIST);
    }

    @Test
    public void getDrivesForUserAsAdmin() {
        login(ADMIN_CREDENTIALS);
        List<DriveWrap> drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVEWRAP_LIST);
        assertTrue(drives.isEmpty());

        //Add user to drive
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        Drive drive1 = new Drive(0, "A", "F", System.currentTimeMillis(), System.currentTimeMillis() + 1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        drive1 = driveDao.addDrive(drive1);
        int drive1Id = drive1.getDriveId();
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, TEST.getId(), "A", "B", false, false, false);

        login(ADMIN_CREDENTIALS);
        drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVEWRAP_LIST);
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDrive().getDriveId(), drive1Id);
    }

    @Test
    public void addUserToDrive() {
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        Drive drive1 = new Drive(0, "A", "F", 1, 2, "x", "x", "x", "x", "x", 2, 1, false, false, false);
        Drive drive2 = driveDao.addDrive(drive1);
        int drive2Id = drive2.getDriveId();

        login(TEST_CREDENTIALS);
        DriveWrap wrap = target("drive")
                .path(Integer.toString(drive2Id))
                .request()
                .get(DriveWrap.class);
        for (DriveUser user : wrap.getUsers()) {
            assertNotEquals(user.getUserId(), TEST.getId());
        }
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive2Id, TEST.getId(), "A", "B", false, false, false);
        wrap = target("drive")
                .path(Integer.toString(drive2Id))
                .request()
                .get(DriveWrap.class);
        for (DriveUser user : wrap.getUsers()) {
            assertEquals(user.getUserId(), TEST.getId());
        }
    }

    @Test
    public void removeUserFromDrive() {
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0).getTime();
        long arrivalTime = new Timestamp(2018 - 1900, 10, 20, 12, 25, 0, 0).getTime();
        Drive drive1 = driveDao.addDrive(new Drive(-1, "A", "F", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        int drive1Id = drive1.getDriveId();

        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());
        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);
        User user2 = userDao.addUser(SEARCH_TEST_CREDENTIALS_2);
        int user1Id = user1.getId();
        int user2Id = user2.getId();

        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, user1Id, "A", "C", true, true, false);
        driveUserDao.addDriveUser(drive1Id, user2Id, "A", "C", false, true, false);

        login(SEARCH_TEST_CREDENTIALS_1);
        target("drive")
                .path(Integer.toString(drive1Id) + "/user/" + Integer.toString(user1Id))
                .request()
                .delete(Void.class);
        logout();
        login(ADMIN_CREDENTIALS);
        DriveWrap wrap = target("drive")
                .path(Integer.toString(drive1Id))
                .request()
                .get(DriveWrap.class);
        for (DriveUser user : wrap.getUsers()) {
            assertNotEquals(user.getUserId(), user1Id);
        }
    }
}
