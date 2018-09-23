package se.lth.base.server.data;

public class DriveReport {
	private final int reportId, driveId, reportedByUserId;
	private final String reportMessage;
	
	public DriveReport(int reportId, int driveId, int reportedByUserId, String reportMessage) {
		this.reportId = reportId;
		this.driveId = driveId;
		this.reportedByUserId = reportedByUserId;
		this.reportMessage = reportMessage;
	}
	
	public int getReportId() {
		return reportId;
	}
	
	public int getDriveId() {
		return driveId;
	}
	
	public int getReportedByUserId() {
		return reportedByUserId;
	}
	
	public String getReportMessage() {
		return reportMessage;
	}
}
