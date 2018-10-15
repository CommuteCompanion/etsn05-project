package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.junit.Assert.*;


public class DriveResourceTest extends BaseResourceTest {

    private static final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>() {
	    };

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
        List<Drive> drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVE_LIST);
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
                .get(DRIVE_LIST);
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDriveId(), drive1Id);
    }

    @Test(expected = WebApplicationException.class)
    public void getDrivesForUserAsWrongUser() {
        login(TEST_CREDENTIALS);
        List<Drive> drives = target("drive")
                .path("user/" + ADMIN.getId())
                .request()
                .get(DRIVE_LIST);
    }

    @Test
    public void getDrivesForUserAsAdmin() {
        login(ADMIN_CREDENTIALS);
        List<Drive> drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVE_LIST);
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
                .get(DRIVE_LIST);
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDriveId(), drive1Id);
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
}
