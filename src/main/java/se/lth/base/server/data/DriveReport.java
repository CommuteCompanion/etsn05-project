package se.lth.base.server.data;

public class DriveReport {
	private final int reportId, driverId, reportedByUserId;
	private final String reportMessage;
	
	public DriveReport(int reportId, int driverId, int reportedByUserId, String reportMessage) {
		this.reportId = reportId;
		this.driverId = driverId;
		this.reportedByUserId = reportedByUserId;
		this.reportMessage = reportMessage;
	}
	
	public int getReportId() {
		return reportId;
	}
	
	public int getDriverId() {
		return driverId;
	}
	
	public int getReportedByUserId() {
		return reportedByUserId;
	}
	
	public String getReportMessage() {
		return reportMessage;
	}
}
