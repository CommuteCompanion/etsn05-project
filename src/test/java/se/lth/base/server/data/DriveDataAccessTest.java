package se.lth.base.server.data;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * @author Group 1 ETSN05 2018
 */

public class DriveDataAccessTest extends BaseDataAccessTest {

    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());

    private int drive1Id;
    private Drive drive1;

    @Before
    public void registerTestDrive() {
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0).getTime();
        long arrivalTime = new Timestamp(2018 - 1900, 10, 20, 12, 25, 0, 0).getTime();
        drive1 = driveDao.addDrive(new Drive(-1, "A", "F", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        drive1Id = drive1.getDriveId();

    }

    @Test
    public void addDrive() {
        List<Drive> drives = driveDao.getDrives();
        for (Drive d : drives) {
            assertEquals(d.getDriveId(), drive1Id);
        }
    }

    @Test
    public void updateDrive() {
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0).getTime();
        long arrivalTime = new Timestamp(2018 - 1900, 10, 20, 12, 25, 0, 0).getTime();
        Drive drive2 = driveDao.addDrive(new Drive(-1, "A", "B", departureTime, arrivalTime, "i", "j", "k", "l", "m", 3, 2, false, false, false));

        assertEquals("A", drive2.getStart());
        assertEquals("B", drive2.getStop());
        assertEquals(departureTime, drive2.getDepartureTime());
        assertEquals(arrivalTime, drive2.getArrivalTime());
        assertEquals("i", drive2.getComment());
        assertEquals("j", drive2.getCarBrand());
        assertEquals("k", drive2.getCarModel());
        assertEquals("l", drive2.getCarColor());
        assertEquals("m", drive2.getCarLicensePlate());
        assertEquals(3, drive2.getCarNumberOfSeats());
        assertEquals(2, drive2.getOptLuggageSize());
        assertEquals(false, drive2.getOptWinterTires());
        assertEquals(false, drive2.getOptBicycle());
        assertEquals(false, drive2.getOptPets());
    }

    @Test
    public void getReportedDrives() {
        int idStart = 3;
        User SEARCH_TEST_1 = new User(idStart, Role.USER, "test1@commutecompanion.se", "ST1FirstName", "ST1LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
        Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("test1@commutecompanion.se", "test", Role.USER, SEARCH_TEST_1);

        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);


        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);
        DriveReportDataAccess driveReportDao = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
        driveReportDao.addDriveReport(drive1Id, user1.getId(), "Bad driver");
        assertEquals(1, driveDao.getReportedDrives().size());
    }

    @Test
    public void getNumberOfDrivesForUser() {
        int idStart = 3;
        User SEARCH_TEST_1 = new User(idStart, Role.USER, "test1@commutecompanion.se", "ST1FirstName", "ST1LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
        Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("test1@commutecompanion.se", "test", Role.USER, SEARCH_TEST_1);

        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);


        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);

        assertEquals(1, driveDao.getNumberOfDrivesForUser(user1.getId()));

    }

    @Test
    public void getDrivesForUser() {
        int idStart = 3;
        User SEARCH_TEST_1 = new User(idStart, Role.USER, "test1@commutecompanion.se", "ST1FirstName", "ST1LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
        Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("test1@commutecompanion.se", "test", Role.USER, SEARCH_TEST_1);

        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());
        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);

        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0).getTime();
        long arrivalTime = new Timestamp(2018 - 1900, 10, 20, 12, 25, 0, 0).getTime();
        Drive drive2 = driveDao.addDrive(new Drive(-1, "A", "B", departureTime, arrivalTime, "i", "j", "k", "l", "m", 3, 2, false, false, false));

        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());

        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);
        driveUserDao.addDriveUser(drive2.getDriveId(), user1.getId(), "A", "B", true, true, false);
        assertEquals(2, driveDao.getNumberOfDrivesForUser(user1.getId()));
    }

    @Test
    public void deleteDrive() {
        driveDao.deleteDrive(drive1Id);
        List<Drive> drives = driveDao.getDrives();
        for (Drive d : drives) {
            assertNotEquals(d.getDriveId(), drive1Id);
        }

    }
}
