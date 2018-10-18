package se.lth.base.server.data;

import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;

public class DriveUserDataAccessTest extends BaseDataAccessTest {

	private final DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
	private static Drive drive;
	
	@Before
	public void addDrive() {
		DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
		drive = driveDao.addDrive(new Drive(1, "Malmö", "Göteborg", 0, 1,
				"Kommer gå fort", "Bugatti", "Veyron Super Sport", "Vit", "1337", 2, 1, false, false, false));
	}
	
	@Test
	public void addDriveUser() {
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), true, true, false);
		List<DriveUser> driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId()); 
		assertTrue(driveUsers.stream().anyMatch(u -> u.getUserId() == TEST.getId()));
	}
	
	@Test
	public void deleteDriveUser() {
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), true, true, false);
		List<DriveUser> driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId());
		assertTrue(driveUsers.stream().anyMatch(u -> u.getUserId() == TEST.getId()));
		driveUserDao.deleteDriveUser(drive.getDriveId(), TEST.getId());
		driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId());
		assertFalse(driveUsers.stream().anyMatch(u -> u.getUserId() == TEST.getId()));
	}
	
	@Test
	public void numberOfAcceptedUsers() {
		driveUserDao.addDriveUser(drive.getDriveId(), ADMIN.getId(), drive.getStart(), drive.getStop(), true, true, false);
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), false, false, false);
		assertEquals(1, driveUserDao.getNumberOfUsersInDrive(drive.getDriveId()));
	}
	
	@Test
	public void acceptDriveUser() {
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), false, false, false);
		assertEquals(0, driveUserDao.getNumberOfUsersInDrive(drive.getDriveId()));
		driveUserDao.acceptDriveUser(drive.getDriveId(), TEST.getId());
		assertEquals(1, driveUserDao.getNumberOfUsersInDrive(drive.getDriveId()));
	}
	
	@Test
	public void updateDriveUser() {
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), true, true, false);
		assertEquals(1, driveUserDao.getNumberOfUsersInDrive(drive.getDriveId()));
		driveUserDao.updateDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), true, false, true);
		assertTrue(!driveUserDao.getDriveUser(drive.getDriveId(), TEST.getId()).isAccepted());
	}
	
	@Test
	public void noAcceptedUsers() {
		driveUserDao.addDriveUser(drive.getDriveId(), TEST.getId(), drive.getStart(), drive.getStop(), false, false, false);
		assertEquals(0, driveUserDao.getNumberOfUsersInDrive(drive.getDriveId()));
	}
}
