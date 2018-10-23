package se.lth.base.server.rest;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.client.Entity;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SearchResourceTest extends BaseResourceTest {

    // Search test users
    private final int idStart = 3;
    private final User SEARCH_TEST_1 = new User(idStart, Role.USER, "test1@commutecompanion.se", "ST1FirstName", "ST1LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_1 = new Credentials("test1@commutecompanion.se", "test", Role.USER, SEARCH_TEST_1);

    private final User SEARCH_TEST_2 = new User(idStart, Role.USER, "test2@commutecompanion.se", "ST2FirstName", "ST2LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_2 = new Credentials("test2@commutecompanion.se", "test", Role.USER, SEARCH_TEST_2);

    private final User SEARCH_TEST_3 = new User(idStart, Role.USER, "test3@commutecompanion.se", "ST3FirstName", "ST3LastName", "+4670207579", 0, Date.valueOf("1995-01-01").getTime(), true, 0, 0, 0);
    private final Credentials SEARCH_TEST_CREDENTIALS_3 = new Credentials("test3@commutecompanion.se", "test", Role.USER, SEARCH_TEST_3);

    private int user1Id;
    private int user2Id;
    private int user3Id;

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
        user1Id = user1.getId();
        user2Id = user2.getId();
        user3Id = user3.getId();

        // Data access objects
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());

        // Drive 1
        Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestamp1 = Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestamp1_5 = Timestamp.valueOf("2019-10-20 12:25:00").getTime();
        Drive drive1 = driveDao.addDrive(new Drive(-1, "A", "F", timestamp1, timestamp1_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        drive1Id = drive1.getDriveId();
        
        long timestamp1_1 = Timestamp.valueOf("2019-10-20 12:05:00").getTime();
        long timestamp1_2 = Timestamp.valueOf("2019-10-20 12:10:00").getTime();
        long timestamp1_3 = Timestamp.valueOf("2019-10-20 12:15:00").getTime();
        long timestamp1_4 = Timestamp.valueOf("2019-10-20 12:20:00").getTime();
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "B", timestamp1_1);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "C", timestamp1_2);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "D", timestamp1_3);
        driveMilestoneDao.addMilestone(drive1.getDriveId(), "E", timestamp1_4);

        driveUserDao.addDriveUser(drive1.getDriveId(), user1.getId(), "A", "F", true, true, false);

        // Drive 2
        long timestamp2 =Timestamp.valueOf("2019-10-20 13:00:0").getTime();
        long timestamp2_5 = Timestamp.valueOf("2019-10-20 13:25:00").getTime();
        Drive drive2 = driveDao.addDrive(new Drive(-1, "A", "F", timestamp2, timestamp2_5, "Comment", "x", "x", "x", "x", 3, 1, false, false, false));
        drive2Id = drive2.getDriveId();
        long timestamp2_1 = Timestamp.valueOf("2019-10-20 13:05:00").getTime();
        long timestamp2_2 = Timestamp.valueOf("2019-10-20 13:10:00").getTime();
        long timestamp2_3 = Timestamp.valueOf("2019-10-20 13:15:00").getTime();
        long timestamp2_4 = Timestamp.valueOf("2019-10-20 13:20:00").getTime();
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "B", timestamp2_1);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "C", timestamp2_2);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "D", timestamp2_3);
        driveMilestoneDao.addMilestone(drive2.getDriveId(), "E", timestamp2_4);

        driveUserDao.addDriveUser(drive2.getDriveId(), user1.getId(), "A", "F", true, true, false);

        // Drive 3
        long timestamp3 = Timestamp.valueOf("2019-10-20 14:00:00").getTime();
        long timestamp3_5 = Timestamp.valueOf("2018-10-20 14:25:00").getTime();
        Drive drive3 = driveDao.addDrive(new Drive(-1, "A2", "F2", timestamp3, timestamp3_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        drive3Id = drive3.getDriveId();
        long timestamp3_1 = Timestamp.valueOf("2019-10-20 14:05:00").getTime();
        long timestamp3_2 = Timestamp.valueOf("2019-10-20 14:10:00").getTime();
        long timestamp3_3 = Timestamp.valueOf("2019-10-20 14:15:00").getTime();
        long timestamp3_4 = Timestamp.valueOf("2019-10-20 14:20:00").getTime();
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "B2", timestamp3_1);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "C2", timestamp3_2);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "D2", timestamp3_3);
        driveMilestoneDao.addMilestone(drive3.getDriveId(), "E2", timestamp3_4);

        driveUserDao.addDriveUser(drive3.getDriveId(), user1.getId(), "A2", "F2", true, true, false);
    }

    @Test
    /*
        Start and stop is entered, departure time is null
     */
    public void getDrivesMatchingStartStop() {
        login(SEARCH_TEST_CREDENTIALS_2);

        SearchFilter searchFilter = new SearchFilter(-1, -1, "C", "E", -1);
        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<DriveWrap> response = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter), List.class);

        Assert.assertEquals(2, response.size());
    }

    /*
        Drive is A -> E (with max 1 passenger)
        Add one passenger
        Make sure that drive is not included in result from getDrives(SearchFilter)
     */
    @Test
    public void verifyUserNotReceivingFullDrives() {
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        driveUserDao.addDriveUser(drive1Id, user2Id, "A", "E", false, true, false);

        login(SEARCH_TEST_CREDENTIALS_2);
        long timestamp1User = Timestamp.valueOf("2019-01-01 20:00:00").getTime();
        SearchFilter searchFilter1 = new SearchFilter(-1, -1, "A", "E", timestamp1User);

        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<DriveWrap> response1 = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

        Assert.assertEquals(0, response1.size());
    }

    @Test
    /*
        New trip from B -> E (13.00) should match with drive 2 (A - E) (C - 13.05)
        New trip from B -> E (17.00) should not match with any drive
     */
    public void getDrivesMatchingTime() {
        login(SEARCH_TEST_CREDENTIALS_2);

        long timestamp1User = Timestamp.valueOf("2019-10-20 13:00:00").getTime();
        SearchFilter searchFilter1 = new SearchFilter(-1, user2Id, "B", "C", timestamp1User);

        // We actually receive a List<LinkedTreeMap<String, Object>>
        List response1 = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

        Assert.assertEquals(1, response1.size());

        long timestamp2User = Timestamp.valueOf("2019-10-20 17:00:00").getTime();
        SearchFilter searchFilter2 = new SearchFilter(-1, user2Id, "B", "C", timestamp2User);

        // We actually receive a List<LinkedTreeMap<String, Object>>
        List response2 = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter2), List.class);

        Assert.assertEquals(0, response2.size());
    }

    @Test
    /*
      When tripStart, tripStop and departureTime in SearchFilter is null and getDrives(SearchFilter) is called,
      all drives should be returned in most recently created order
     */
    public void getDrivesInMostRecentlyCreatedOrder() {
        login(SEARCH_TEST_CREDENTIALS_2);

        SearchFilter searchFilter1 = new SearchFilter(-1, user2Id, null, null, -1);

        @SuppressWarnings("unchecked")
        List<LinkedTreeMap<String, Object>> response = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter1), List.class);

                // Apparently the list contains LinkedTreeMap<String, Object>, each representing a DriveWrap object
        @SuppressWarnings("unchecked")
        LinkedTreeMap<String, Object> drive3 = (LinkedTreeMap<String, Object>) (response.get(0)).get("drive");
        Assert.assertEquals((double) drive3Id, drive3.get("driveId"));
        @SuppressWarnings("unchecked")
        LinkedTreeMap<String, Object> drive2 = (LinkedTreeMap<String, Object>) (response.get(1)).get("drive");
        Assert.assertEquals((double) drive2Id, drive2.get("driveId"));
        @SuppressWarnings("unchecked")
        LinkedTreeMap<String, Object> drive1 = (LinkedTreeMap<String, Object>) (response.get(2)).get("drive");
        Assert.assertEquals((double) drive1Id, drive1.get("driveId"));
    }

    @Test
    /*
     * 1.Search for first search test user
     * 2.Get all search test users (by specifying part of first name)
     * 3.Get all search test users (by specifying part of first name and part of email)
     */
    public void getUsersMatching() {
        login(ADMIN_CREDENTIALS);

        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<User> response1 = target("search")
                .path("getUsers/ST1FirstName ST1LastName/test1@commutecompanion.se")
                .request()
                .get(List.class);

        Assert.assertEquals(1, response1.size());

        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<User> response2 = target("search")
                .path("getUsers/First/")
                .request()
                .get(List.class);

        Assert.assertEquals(3, response2.size());

        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<User> response3 = target("search")
                .path("getUsers/First/commute")
                .request()
                .get(List.class);

        Assert.assertEquals(3, response3.size());
    }

    @Test
    /*
     * 1. Drive B has lowest diff in departure time vs users departure time (driver1)
     * 2. Drive D has a little later departure time, but better rating than drive C (driver3)
     * 3. Drive C has the same departure time as drive D. (driver1)
     * 4. Drive A has longest diff in depart time (driver1)
     *
     */
    public void testPrioritizedOrder() {
        DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
        DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
        UserDataAccess userDataAccess = new UserDataAccess(Config.instance().getDatabaseDriver());

        userDataAccess.updateUserRating(new DriveRating(user1Id, 3));
        userDataAccess.updateUserRating(new DriveRating(user3Id, 4));

        // Drive A
        Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestampA = Timestamp.valueOf("2019-10-20 03:30:00").getTime();
        long timestampA_5 = Timestamp.valueOf("2019-10-20 04:00:00").getTime();
        Drive driveA = driveDao.addDrive(new Drive(-1, "A", "B", timestampA, timestampA_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        int driveAId = driveA.getDriveId();
        driveUserDao.addDriveUser(driveAId, user1Id, "A", "B", true, true, false);

        // Drive B
        Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestampB = Timestamp.valueOf("2019-10-20 03:00:00").getTime();
        long timestampB_5 = Timestamp.valueOf("2019-10-20 04:00:00").getTime();
        Drive driveB = driveDao.addDrive(new Drive(-1, "A", "B", timestampB, timestampB_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        int driveBId = driveB.getDriveId();
        driveUserDao.addDriveUser(driveBId, user1Id, "A", "B", true, true, false);

        // Drive C
        Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestampC = Timestamp.valueOf("2019-10-20 03:15:00").getTime();
        long timestampC_5 = Timestamp.valueOf("2019-10-20 04:00:00").getTime();
        Drive driveC = driveDao.addDrive(new Drive(-1, "A", "B", timestampC, timestampC_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        int driveCId = driveC.getDriveId();
        driveUserDao.addDriveUser(driveCId, user1Id, "A", "B", true, true, false);

        // Drive D (same as drive C but for driver with better rating)
        Timestamp.valueOf("2019-10-20 12:00:00").getTime();
        long timestampD = Timestamp.valueOf("2019-10-20 03:15:00").getTime();
        long timestampD_5 = Timestamp.valueOf("2019-10-20 04:00:00").getTime();
        Drive driveD = driveDao.addDrive(new Drive(-1, "A", "B", timestampD, timestampD_5, "Comment", "x", "x", "x", "x", 1, 1, false, false, false));
        int driveDId = driveD.getDriveId();
        driveUserDao.addDriveUser(driveDId, user3Id, "A", "B", true, true, false);

        long timestamp1User = Timestamp.valueOf("2019-10-20 03:00:00").getTime();
        SearchFilter searchFilter = new SearchFilter(-1, user2Id, "A", "B", timestamp1User);

        login(SEARCH_TEST_CREDENTIALS_2);

        // We actually receive a List<LinkedTreeMap<String, Object>>
        @SuppressWarnings("unchecked")
        List<LinkedTreeMap<String, Object>> response1 = target("search")
                .path("drives")
                .request()
                .post(Entity.json(searchFilter), List.class);

        List<Integer> driveIdOrder = new ArrayList<>();
        for (LinkedTreeMap<String, Object> driveWrap : response1) {
            @SuppressWarnings("unchecked")
            LinkedTreeMap<String, Object> drive = (LinkedTreeMap<String, Object>) driveWrap.get("drive");
            int driveId = (int) Math.round((Double) drive.get("driveId"));
            driveIdOrder.add(driveId);
        }

        Assert.assertEquals(driveBId, (int) driveIdOrder.get(0));
        Assert.assertEquals(driveDId, (int) driveIdOrder.get(1));
        Assert.assertEquals(driveCId, (int) driveIdOrder.get(2));
        Assert.assertEquals(driveAId, (int) driveIdOrder.get(3));
    }

}
