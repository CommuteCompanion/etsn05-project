package se.lth.base.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

/**
 * Data access class for a drive milestone
 * 
 * @author Group 1 ETSN05 2018
 *
 */
public class DriveMilestoneDataAccess extends DataAccess<DriveMilestone> {
	private static final class MilestoneMapper implements Mapper<DriveMilestone> {
		@Override
		public DriveMilestone map(ResultSet resultSet) throws SQLException {
			return new DriveMilestone(resultSet.getInt("milestone_id"),
					resultSet.getInt("drive_id"),
					resultSet.getString("milestone"));
		}
	}
	
	public DriveMilestoneDataAccess(String driverUrl) {
        super(driverUrl, new MilestoneMapper());
    }
	
	public DriveMilestone addMilestone(int driveId, String milestone) {
		int milestoneId = insert("INSERT INTO drive_milestone (drive_id, order, stop) VALUES(?,?,?");
		return new DriveMilestone(milestoneId, driveId, milestone);
	}
	
	public DriveMilestone updateMilestone(int milestoneId, String milestone) {
		execute("UPDATE drive_milestone SET milestone = ? WHERE milestoneId = ?", milestone, milestoneId);
		return getMilestone(milestoneId);
	}
	
	public DriveMilestone getMilestone(int milestoneId) {
		return queryFirst("SELECT milestone_id, drive_id, milestone FROM drive_milestone WHERE milestone_id = ?", milestoneId);
	}
	
	public List<DriveMilestone> getMilestoneForDrive(int driveId) {
		return query("SELECT milestone_id, drive_id, milestone FROM drive_milestone WHERE drive_id = ?", driveId);
	}
	
	public boolean deleteMilestone(int milestoneId) {
		return execute("DELETE FROM drive_milestone WHERE milestone_id = ?", milestoneId) > 0;
	}
}
