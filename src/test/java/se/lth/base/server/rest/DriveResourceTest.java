package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.Drive;
import se.lth.base.server.data.DriveDataAccess;
import se.lth.base.server.data.DriveUserDataAccess;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
        Timestamp timestamp1 = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        Drive drive1 = driveDao.addDrive("A", "F", timestamp1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        int drive1Id = drive1.getDriveId();
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, TEST.getId(), "A", "B", false, false, false);

        //New drive with a new user
        Drive drive2 = driveDao.addDrive("A", "F", timestamp1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        int drive2Id = drive2.getDriveId();
        driveUserDao.addDriveUser(drive2Id, ADMIN.getId(), "A", "B", false, false, false);

        login(TEST_CREDENTIALS);
        drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVE_LIST);
        assertEquals(1, drives.size());

    }

    //
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
        Timestamp timestamp1 = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        Drive drive1 = driveDao.addDrive("A", "F", timestamp1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        int drive1Id = drive1.getDriveId();
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, TEST.getId(), "A", "B", false, false, false);

        login(ADMIN_CREDENTIALS);
        drives = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVE_LIST);
        assertEquals(1, drives.size());
    }
}
