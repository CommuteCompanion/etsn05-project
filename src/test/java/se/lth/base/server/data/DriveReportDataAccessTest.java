package se.lth.base.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;

import java.sql.Timestamp;
import java.util.List;

public class DriveReportDataAccessTest extends BaseDataAccessTest {

    private final DriveReportDataAccess driveReportDao = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());

    private Drive drive;

    @Before
    public void createDrive() {
        long departureTime = Timestamp.valueOf("2018-01-01 20:00:00").getTime();
        long arrivalTime = Timestamp.valueOf("2018-01-01 20:00:00").getTime();
        drive = new Drive(-1, "A", "C", departureTime, arrivalTime, "c", "x", "y", "z", "x1", 1, 1, false, false, false);
        drive = driveDao.addDrive(drive);
    }

    @Test
    // Tests addDriveReport(...), getDriveReportsForDrive(int), deleteDriveReport(int)
    public void addAndDeleteDriveReport() {
        // Check for empty list
        List<DriveReport> driveReports = driveReportDao.getDriveReportsForDrive(drive.getDriveId());
        Assert.assertEquals(0, driveReports.size());

        // Add drive report
        DriveReport driveReport = driveReportDao.addDriveReport(drive.getDriveId(), TEST.getId(), "Lorem ipsum");

        // Check for non-empty list and validate attributes
        driveReports = driveReportDao.getDriveReportsForDrive(drive.getDriveId());
        Assert.assertEquals(1, driveReports.size());
        driveReport = driveReports.get(0);
        Assert.assertEquals(drive.getDriveId(), driveReport.getDriveId());
        Assert.assertEquals(TEST.getId(), driveReport.getReportedByUserId());
        Assert.assertEquals("Lorem ipsum", driveReport.getReportMessage());

        // Delete drive report
        Assert.assertTrue(driveReportDao.deleteDriveReport(driveReport.getReportId()));

        // Check for empty list
        driveReports = driveReportDao.getDriveReportsForDrive(drive.getDriveId());
        Assert.assertEquals(0, driveReports.size());
    }


    @Test
    // Test getDriveReport(int)
    public void getDriveReport() {
        // Add drive report
        DriveReport driveReport1 = driveReportDao.addDriveReport(drive.getDriveId(), TEST.getId(), "Lorem ipsum");

        // Get added drive report
        DriveReport driveReport2 = driveReportDao.getDriveReport(driveReport1.getReportId());
        Assert.assertNotNull(driveReport2);

        // Validate attributes
        Assert.assertEquals(driveReport1.getReportId(), driveReport2.getReportId());
        Assert.assertEquals(driveReport1.getDriveId(), driveReport2.getDriveId());
        Assert.assertEquals(driveReport1.getReportedByUserId(), driveReport2.getReportedByUserId());
        Assert.assertEquals(driveReport1.getReportMessage(), driveReport2.getReportMessage());
    }


}
