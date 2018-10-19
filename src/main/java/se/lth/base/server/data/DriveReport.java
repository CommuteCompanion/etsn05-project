package se.lth.base.server.data;

/**
 * Data class for a drive report
 *
 * @author Group 1 ETSN05 2018
 * @see DriveReportDataAccess
 */
public class DriveReport {
	private final int reportId, driveId, reportedByUserId;
	private final String reportMessage;

    /**
     * @param reportId         the Id of the report.
     * @param driveId          the Id of the drive.
     * @param reportedByUserId the Id of the user that reported.
     * @param reportMessage    the text message of the report.
     */
	public DriveReport(int reportId, int driveId, int reportedByUserId, String reportMessage) {
		this.reportId = reportId;
		this.driveId = driveId;
		this.reportedByUserId = reportedByUserId;
		this.reportMessage = reportMessage;
    }

    /** @return the Id of the report. */
	public int getReportId() {
        return reportId;
    }

    /** @return the Id of the drive. */
	public int getDriveId() {
        return driveId;
    }

    /** @return the text message of the report. */
	public int getReportedByUserId() {
        return reportedByUserId;
    }

    /** @return the Id of the user that reported. */
	public String getReportMessage() {
		return reportMessage;
	}
}
