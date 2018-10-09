package se.lth.base.server.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class SearchResourceTest extends BaseResourceTest {

    private final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>(){};

    private final int idStart = 3;
    private final User SEARCH_TEST_1 = new User(idStart, Role.USER, "SearchTest1", "SearchTest1", "Smith", "+4670207579", "test1@commutecompanion.se", 0, Date.valueOf("1995-01-01"), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("SearchTest1", "test", Role.USER, SEARCH_TEST_1);


    private final User SEARCH_TEST_2 = new User(idStart + 1, Role.USER, "SearchTest2", "SearchTest2", "Smith", "+4670207579", "test2@commutecompanion.se", 0, Date.valueOf("1995-01-01"), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_2 = new Credentials("SearchTest2", "test", Role.USER, SEARCH_TEST_2);

    private final User SEARCH_TEST_3 = new User(idStart + 2, Role.USER, "SearchTest3", "SearchTest3", "Smith", "+4670207579", "test3@commutecompanion.se", 0, Date.valueOf("1995-01-01"), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_3 = new Credentials("SearchTest3", "test", Role.USER, SEARCH_TEST_3);


    private int user1Id;
    private int user2Id;
    private int user3Id;

    private int drive1Id;

    @Before
    public void registerTestUsers() {
        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);
        User user2 = userDao.addUser(SEARCH_TEST_CREDENTIALS_2);
        User user3 = userDao.addUser(SEARCH_TEST_CREDENTIALS_3);
        user1Id = user1.getId();
        user2Id = user2.getId();
        user3Id = user3.getId();

        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        Timestamp timestamp1 = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        Drive drive1 = driveDao.addDrive("A", "F", timestamp1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        drive1Id = drive1.getDriveId();
        Timestamp timestamp1_1 = new Timestamp(2018 - 1900, 10, 20, 12, 5, 0, 0);
        Timestamp timestamp1_2 = new Timestamp(2018 - 1900, 10, 20, 12, 5, 0, 0);
        Timestamp timestamp1_3 = new Timestamp(2018 - 1900, 10, 20, 12, 5, 0, 0);
        Timestamp timestamp1_4 = new Timestamp(2018 - 1900, 10, 20, 12, 5, 0, 0);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "B", timestamp1_1);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "C", timestamp1_2);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "D", timestamp1_3);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "E", timestamp1_4);
        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);

    }

    @Test
    public void getDrivesMatching() {
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, user2Id, "A", "C", false, false, false);

        login(SEARCH_TEST_CREDENTIALS_2);

        Timestamp timestampUser1 = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        SearchFilter searchFilter = new SearchFilter(-1, -1, "C", "E", timestampUser1);

        List<Drive> response = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter), List.class);

        Assert.assertEquals(1, response.size());
    }


}
