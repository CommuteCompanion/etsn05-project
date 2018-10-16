package se.lth.base.server.data;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access class for a drive report
 * 
 * @author Group 1 ETSN05 2018
 *
 */
public class DriveReportDataAccess extends DataAccess<DriveReport> {
    public DriveReport getDriveReport(int reportId) {
        return queryFirst("SELECT report_id, drive_id, reported_by_user_id, report_message FROM drive_report WHERE report_id = ?", reportId);
    }

    public DriveReportDataAccess(String driverUrl) {
        super(driverUrl, new DriveMapper());
    }
    
    public DriveReport addDriveReport(int driveId, int reportedByUserId, String reportMessage) {
    	int reportId = insert("INSERT INTO drive_report (drive_id, reported_by_user_id, report_message) VALUES (?,?,?)",
    			driveId, reportedByUserId, reportMessage);
    	
    	return new DriveReport(reportId, driveId, reportedByUserId, reportMessage);
    }
    
    public List<DriveReport> getDriveReportsForDrive(int driveId) {
        return query("SELECT report_id, drive_id, reported_by_user_id, report_message FROM drive_report WHERE drive_id = ?", driveId);
    }

    private static final class DriveMapper implements Mapper<DriveReport> {
        @Override
        public DriveReport map(ResultSet resultSet) throws SQLException {
            return new DriveReport(resultSet.getInt("report_id"),
                    resultSet.getInt("drive_id"),
                    resultSet.getInt("reported_by_user_id"),
                    resultSet.getString("report_message"));
        }
    }
    
    public boolean deleteDriveReport(int reportId) {
        return execute("DELETE FROM drive_report WHERE report_id = ?", reportId) > 0;
    }
}



