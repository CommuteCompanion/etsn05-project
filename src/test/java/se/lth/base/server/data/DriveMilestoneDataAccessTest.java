package se.lth.base.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;

import java.sql.Timestamp;
import java.util.List;

public class DriveMilestoneDataAccessTest extends BaseResourceTest {

    private final DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());

    private Drive drive;


    @Before
    public void createDrive() {
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 0, 0, 0).getTime();
        long arrivalTime = new Timestamp(2018 - 1900, 10, 20, 13, 0, 0, 0).getTime();
        drive = new Drive(-1, "A", "C", departureTime, arrivalTime, "c", "x", "y", "z", "x1", 1, 1, false, false, false);
        drive = driveDao.addDrive(drive);
    }

    @Test
    // Tests addMilestone(...), getMilestonesForDrive(int), updateMilestone(...) and deleteMilestone(int)
    public void addUpdateAndDeleteMilestone() {
        // Check for empty list
        List<DriveMilestone> driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
        Assert.assertEquals(0, driveMilestones.size());

        // Add a milestone to the drive
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 30, 0, 0).getTime();
        driveMilestoneDao.addMilestone(drive.getDriveId(), "B", departureTime);

        // Check for non empty list and verify DriveMilestone attributes
        driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
        Assert.assertEquals(1, driveMilestones.size());
        DriveMilestone driveMilestone = driveMilestones.get(0);
        Assert.assertEquals(drive.getDriveId(), driveMilestone.getDriveId());
        Assert.assertEquals("B", driveMilestone.getMilestone());
        Assert.assertEquals(departureTime, driveMilestone.getDepartureTime());

        // Update DriveMilestone and validate attributes
        long newDepartureTime = new Timestamp(2018 - 1900, 10, 20, 12, 45, 0, 0).getTime();
        driveMilestoneDao.updateMilestone(driveMilestone.getMilestoneId(), "B1", newDepartureTime);
        driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
        Assert.assertEquals(1, driveMilestones.size());
        driveMilestone = driveMilestones.get(0);
        Assert.assertEquals(drive.getDriveId(), driveMilestone.getDriveId());
        Assert.assertEquals("B1", driveMilestone.getMilestone());
        Assert.assertEquals(newDepartureTime, driveMilestone.getDepartureTime());


        // Delete milestone from the drive
        driveMilestoneDao.deleteMilestone(driveMilestone.getMilestoneId());

        // Check for empty list
        driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
        Assert.assertEquals(0, driveMilestones.size());
    }

    @Test
    // Tests getMilestone(int)
    public void getMilestone() {
        // Add a milestone to the drive
        long departureTime = new Timestamp(2018 - 1900, 10, 20, 12, 30, 0, 0).getTime();
        DriveMilestone driveMilestone1 = driveMilestoneDao.addMilestone(drive.getDriveId(), "B", departureTime);

        DriveMilestone driveMilestone2 = driveMilestoneDao.getMilestone(driveMilestone1.getMilestoneId());
        Assert.assertEquals(driveMilestone1.getMilestoneId(), driveMilestone2.getDriveId());
        Assert.assertEquals(driveMilestone1.getDriveId(), driveMilestone2.getDriveId());
        Assert.assertEquals(driveMilestone1.getMilestone(), driveMilestone2.getMilestone());
        Assert.assertEquals(driveMilestone1.getDepartureTime(), driveMilestone2.getDepartureTime());
    }


}
