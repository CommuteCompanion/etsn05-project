package se.lth.base.server.rest;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.data.*;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class DriveResourceTest extends BaseResourceTest {

	private static final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>() {
	};
	private static final GenericType<List<DriveWrap>> DRIVEWRAP_LIST = new GenericType<List<DriveWrap>>() {
	};
    
    private DriveWrap driveWrap;
    int driveId;
    
    @Before
    public void setup() {
    	login(TEST_CREDENTIALS);
    	long departureTime = Timestamp.valueOf("2018-01-01 20:00:00").getTime();
        long arrivalTime = Timestamp.valueOf("2018-01-01 21:00:00").getTime();
        Drive drive = new Drive(-1, "A", "B", departureTime, arrivalTime, "Comment", "Brand", "Model", "Color", "License Plate", 4, 1, false, false, false);
        driveWrap = new DriveWrap(drive, new ArrayList<DriveMilestone>(), new ArrayList<DriveUser>(), new ArrayList<DriveReport>());
		driveWrap = target("drive")
				.request()
				.post(Entity.json(driveWrap), DriveWrap.class);
		driveId = driveWrap.getDrive().getDriveId();
		logout();
    }
    
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
        assertTrue(drives.size() == 1);
    }

    @Test
    public void getDrivesForUserAsRightUser() {
        login(TEST_CREDENTIALS);
        List<DriveWrap> driveWraps = target("drive")
                .path("user/" + TEST.getId())
                .request()
                .get(DRIVEWRAP_LIST);
        assertEquals(1, driveWraps.size());
        assertEquals(driveWraps.get(0).getDrive().getDriveId(), driveId);
    }

    @Test(expected = WebApplicationException.class)
    public void getDrivesForUserAsWrongUser() {
        login(TEST_CREDENTIALS);
        target("drive")
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
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDrive().getDriveId(), driveId);
    }

    @Test
    public void removeUserFromDrive() {
		login(ADMIN_CREDENTIALS);
		DriveUser driveUser = new DriveUser(driveId, ADMIN.getId(), "A", "B", false, false, false);
		driveUser = target("drive")
				.path(driveId + "/user")
				.request()
				.post(Entity.json(driveUser), DriveUser.class);
		logout();
		login(TEST_CREDENTIALS);
		target("drive")
              .path(driveId + "/user/" + ADMIN.getId())
              .request()
              .delete(Void.class);
        driveWrap = target("drive")
                .path(Integer.toString(driveId))
                .request()
                .get(DriveWrap.class);
        for(DriveUser du : driveWrap.getUsers()) {
        	assertFalse(du.getUserId() == ADMIN.getId());
        }
    }    
	@Test
	public void createAndUpdateDrive() {
		login(TEST_CREDENTIALS);
		long departureTime = Timestamp.valueOf("2018-01-01 20:00:00").getTime();
        long arrivalTime = Timestamp.valueOf("2018-01-01 21:00:00").getTime();
		Drive drive = new Drive(-1, "A", "F", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 1, 1, true, true, false);
		DriveWrap newDriveWrap= new DriveWrap(drive, new ArrayList<DriveMilestone>(), new ArrayList<DriveUser>(), new ArrayList<DriveReport>());
		newDriveWrap = target("drive")
				.request()
				.post(Entity.json(newDriveWrap), DriveWrap.class);
		DriveWrap actual = target("drive")
                .path(Integer.toString(newDriveWrap.getDrive().getDriveId()))
                .request()
                .get(DriveWrap.class);
		
		//Test if the correct drive was added
		assertEquals(actual.getDrive().getDriveId(), newDriveWrap.getDrive().getDriveId());
		assertEquals(actual.getDrive().getCarLicensePlate(), newDriveWrap.getDrive().getCarLicensePlate());
		
		int driveId = actual.getDrive().getDriveId();
		drive = new Drive(driveId, "A", "B", departureTime, arrivalTime, "Comment", "Audi", "Q8", "White Walker White", "ABC123", 4, 1, false, false, false);
		ArrayList<DriveMilestone> milestones= new ArrayList<DriveMilestone>();
		milestones.add(new DriveMilestone(0, driveId, "C", arrivalTime));
		DriveWrap driveWrap = new DriveWrap(drive, milestones, new ArrayList<DriveUser>(), new ArrayList<DriveReport>());
		DriveWrap updatedDrive = target("drive")
				.path(Integer.toString(driveId))
				.request()
				.put(Entity.json(driveWrap), DriveWrap.class);
		assertEquals("White Walker White", updatedDrive.getDrive().getCarColor());
		assertEquals("C", updatedDrive.getMilestones().get(0).getMilestone());
	}
	
	@Test(expected = WebApplicationException.class)
	public void updateDriveNotAsDriver() {
		login(ADMIN_CREDENTIALS);
		DriveUser du = new DriveUser(driveId, ADMIN.getId(), "A", "F", false, false, false);
		//add user to the drive
		target("drive")
				.path(Integer.toString(driveId) + "/user")
				.request()
				.post(Entity.json(du), DriveUser.class);
		target("drive")
				.path(Integer.toString(driveId))
				.request()
				.put(Entity.json(driveWrap.getDrive()), Drive.class);
	}
	
	@Test(expected = NotFoundException.class)
	public void deleteDrive() {
		login(TEST_CREDENTIALS);
		long departureTime = Timestamp.valueOf("2018-01-01 20:00:00").getTime();
        long arrivalTime = Timestamp.valueOf("2018-01-01 21:00:00").getTime();
		Drive drive = new Drive(-1, "A", "B", departureTime, arrivalTime, "Comment", "x", "x", "x", "x", 2, 1, true, true, false);
		DriveWrap newDriveWrap= new DriveWrap(drive, new ArrayList<DriveMilestone>(), new ArrayList<DriveUser>(), new ArrayList<DriveReport>());
		newDriveWrap = target("drive")
				.request()
				.post(Entity.json(newDriveWrap), DriveWrap.class);
		int driveId = newDriveWrap.getDrive().getDriveId();
		target("drive")
				.path(Integer.toString(driveId))
				.request()
				.delete(Void.class);
		target("drive")
        .path(Integer.toString(driveId))
        .request()
        .get(DriveWrap.class);
    }

    @Test
    public void reportDriveAndgetAllReports() {
    	login(ADMIN_CREDENTIALS);
		//Look for reported drives, expect 0
		List<DriveWrap> reportWraps = target("drive")
				.path("all-reports")
				.request()
				.get(DRIVEWRAP_LIST);
		assertTrue(reportWraps.isEmpty());
		//Add report
		DriveReport report = new DriveReport(-1, driveId, ADMIN.getId(), "Driving like a mad man");
		target("drive")
				.path(Integer.toString(driveId) + "/report")
				.request()
				.post(Entity.json(report), DriveReport.class);
		reportWraps = target("drive")
				.path("all-reports")
				.request()
				.get(DRIVEWRAP_LIST);
		assertEquals("Driving like a mad man", reportWraps.get(0).getReports().get(0).getReportMessage());
    }

    @Test
    public void numberOfDrivesForUser() {
    	login(TEST_CREDENTIALS);
    	
		driveWrap = target("drive")
				.request()
				.post(Entity.json(driveWrap), DriveWrap.class);
		int numberOfDrives = target("drive")
				.path("count/" + TEST.getId())
				.request()
				.get(int.class);
		assertEquals(2, numberOfDrives);
    }
	
    @Test
    public void addAndAcceptUserInDrive() {
    	//add
    	login(ADMIN_CREDENTIALS);
		DriveUser driveUser = new DriveUser(driveId, ADMIN.getId(), "A", "B", false, false, false);
		driveUser = target("drive")
				.path(driveId + "/user")
				.request()
				.post(Entity.json(driveUser), DriveUser.class);
		logout();
		//accept
		login(TEST_CREDENTIALS);
		target("drive")
				.path(driveId + "/user/" + ADMIN.getId())
				.request()
				.put(Entity.json(driveUser));
		driveWrap = target("drive")
        .path(Integer.toString(driveId))
        .request()
        .get(DriveWrap.class);
		assertTrue(driveWrap.getUsers().get(0).isAccepted());
		assertTrue(driveWrap.getUsers().get(1).isAccepted());
    }

    @Test
    public void rateUser() {
		login(ADMIN_CREDENTIALS);
		DriveUser driveUser = new DriveUser(driveId, ADMIN.getId(), "A", "B", false, false, false);
		driveUser = target("drive")
				.path(driveId + "/user")
				.request()
				.post(Entity.json(driveUser), DriveUser.class);
		target("drive")
				.path(driveId + "/rate/" + 4)
				.request()
				.put(Entity.json(4));
		driveWrap = target("drive")
        .path(Integer.toString(driveId))
        .request()
        .get(DriveWrap.class);
		assertTrue(driveWrap.getUsers().get(1).hasRated());
    }
    
}
