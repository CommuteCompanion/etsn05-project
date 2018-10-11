package se.lth.base.server.rest;

import com.google.gson.internal.LinkedTreeMap;
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

    private final User SEARCH_TEST_4 = new User(idStart + 3, Role.USER, "SearchTest4", "SearchTest4", "Smith", "+4670207579", "test4@commutecompanion.se", 0, Date.valueOf("1995-01-01"), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_4 = new Credentials("SearchTest4", "test", Role.USER, SEARCH_TEST_4);

    private final User SEARCH_TEST_5 = new User(idStart + 4, Role.USER, "SearchTest5", "SearchTest5", "Smith", "+4670207579", "test5@commutecompanion.se", 0, Date.valueOf("1995-01-01"), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_5 = new Credentials("SearchTest5", "test", Role.USER, SEARCH_TEST_5);


    private int user1Id;
    private int user2Id;
    private int user3Id;
    private int user4Id;
    private int user5Id;

    private int drive1Id;
    private int drive2Id;
    private int drive3Id;

    @Before
    public void registerTestUsers() {
        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

        // Users
        User user1 = userDao.addUser(SEARCH_TEST_CREDENTIALS_1);
        User user2 = userDao.addUser(SEARCH_TEST_CREDENTIALS_2);
        User user3 = userDao.addUser(SEARCH_TEST_CREDENTIALS_3);
        User user4 = userDao.addUser(SEARCH_TEST_CREDENTIALS_4);
        User user5 = userDao.addUser(SEARCH_TEST_CREDENTIALS_5);
        user1Id = user1.getId();
        user2Id = user2.getId();
        user3Id = user3.getId();
        user4Id = user4.getId();
        user5Id = user5.getId();

        // Data access objects
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());

        // Drive 1
        Timestamp timestamp1 = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        Drive drive1 = driveDao.addDrive("A", "F", timestamp1, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        drive1Id = drive1.getDriveId();
        Timestamp timestamp1_1 = new Timestamp(2018 - 1900, 10, 20, 12, 5, 0, 0);
        Timestamp timestamp1_2 = new Timestamp(2018 - 1900, 10, 20, 12, 10, 0, 0);
        Timestamp timestamp1_3 = new Timestamp(2018 - 1900, 10, 20, 12, 15, 0, 0);
        Timestamp timestamp1_4 = new Timestamp(2018 - 1900, 10, 20, 12, 20, 0, 0);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "B", timestamp1_1);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "C", timestamp1_2);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "D", timestamp1_3);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "E", timestamp1_4);

        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);

        // Drive 2
        Timestamp timestamp2 = new Timestamp(2018 - 1900, 10, 20, 13, 0, 0, 0);
        Drive drive2 = driveDao.addDrive("A", "F", timestamp2, "Comment", "x", "x", "x", "x", 3, 1, false, false, false);
        drive2Id = drive2.getDriveId();
        Timestamp timestamp2_1 = new Timestamp(2018 - 1900, 10, 20, 13, 5, 0, 0);
        Timestamp timestamp2_2 = new Timestamp(2018 - 1900, 10, 20, 13, 10, 0, 0);
        Timestamp timestamp2_3 = new Timestamp(2018 - 1900, 10, 20, 13, 15, 0, 0);
        Timestamp timestamp2_4 = new Timestamp(2018 - 1900, 10, 20, 13, 20, 0, 0);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "B", timestamp2_1);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "C", timestamp2_2);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "D", timestamp2_3);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "E", timestamp2_4);

        driveUserDao.addDriveUser(drive2.getDriveId(), user1.getId(), "A", "F", true, true, false);

        // Drive 3
        Timestamp timestamp3 = new Timestamp(2018 - 1900, 10, 20, 14, 0, 0, 0);
        Drive drive3 = driveDao.addDrive("A2", "F2", timestamp3, "Comment", "x", "x", "x", "x", 1, 1, false, false, false);
        drive3Id = drive3.getDriveId();
        Timestamp timestamp3_1 = new Timestamp(2018 - 1900, 10, 20, 14, 5, 0, 0);
        Timestamp timestamp3_2 = new Timestamp(2018 - 1900, 10, 20, 14, 10, 0, 0);
        Timestamp timestamp3_3 = new Timestamp(2018 - 1900, 10, 20, 14, 15, 0, 0);
        Timestamp timestamp3_4 = new Timestamp(2018 - 1900, 10, 20, 14, 20, 0, 0);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "B2", timestamp3_1);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "C2", timestamp3_2);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "D2", timestamp3_3);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "E2", timestamp3_4);

        driveUserDao.addDriveUser(drive3.getDriveId(), user1.getId(), "A2", "F2", true, true, false);
    }

    @Test
    /*
        Start and stop is entered, depature time is null
     */
    public void getDrivesMatchingStartStop() {
        login(SEARCH_TEST_CREDENTIALS_2);

        SearchFilter searchFilter = new SearchFilter(-1, -1, "C", "E", null);
        List<Drive> response = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter), List.class);

        Assert.assertEquals(2, response.size());
    }

    @Test
    /*
        Drive is A -> E (with max 1 passenger)
        Already one trip from A -> C
        Trip from B -> C should not be possible
        Trip from C -> E should be possible
     */
    public void getDrivesWithEnoughSeats() {
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, user2Id, "A", "C", false, false, false);

        login(SEARCH_TEST_CREDENTIALS_2);
        Timestamp timestamp1User = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        SearchFilter searchFilter1 = new SearchFilter(-1, -1, "B", "C", timestamp1User);

        List<Drive> response1 = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

        Assert.assertEquals(0, response1.size());

        Timestamp timestamp2User = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0);
        SearchFilter searchFilter2 = new SearchFilter(-1, -1, "C", "E", timestamp2User);

        List<Drive> response2 = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter2), List.class);

        Assert.assertEquals(1, response2.size());
    }

    @Test
    /*
        New trip from B -> E (13.00) should match with drive 2 (A - E) (C - 13.05)
        New trip from B -> E (17.00) should not match with any drive
     */

    public void getDrivesMatchingTime() {
        login(SEARCH_TEST_CREDENTIALS_2);

        Timestamp timestamp1User = new Timestamp(2018 - 1900, 10, 20, 13, 0, 0, 0);
        SearchFilter searchFilter1 = new SearchFilter(-1, user2Id, "B", "C", timestamp1User);

        List<Drive> response1 = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

        Assert.assertEquals(1, response1.size());

        Timestamp timestamp2User = new Timestamp(2018 - 1900, 10, 20, 17, 0, 0, 0);
        SearchFilter searchFilter2 = new SearchFilter(-1, user2Id, "B", "C", timestamp2User);

        List<Drive> response2 = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter2), List.class);

        Assert.assertEquals(0, response2.size());


    }

    @Test
    /**
     * When tripStart, tripStop and departureTime in SearchFilter is null and getDrives(SearchFilter) is called,
     * all drives should be returned in most recently created order
     */
    public void getDrivesInMostRecentlyCreatedOrder() {
        login(SEARCH_TEST_CREDENTIALS_2);

        SearchFilter searchFilter1 = new SearchFilter(-1, user2Id, null, null, null);

        List<LinkedTreeMap<String, Object>> response = target("search")
                .path("getDrives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

        // Apparently the list contains LinkedTreeMap<String, Object>, each representing a Drive object
        LinkedTreeMap<String, Object> drive3 = response.get(0);
        Assert.assertEquals((double) drive3Id, drive3.get("driveId"));
        LinkedTreeMap<String, Object> drive2 = response.get(1);
        Assert.assertEquals((double) drive2Id, drive2.get("driveId"));
        LinkedTreeMap<String, Object> drive1 = response.get(2);
        Assert.assertEquals((double) drive1Id, drive1.get("driveId"));
    }
}
