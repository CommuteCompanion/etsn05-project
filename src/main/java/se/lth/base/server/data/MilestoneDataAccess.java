package se.lth.base.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

public class MilestoneDataAccess extends DataAccess<Milestone> {
	private static final class MilestoneMapper implements Mapper<Milestone> {
		@Override
		public Milestone map(ResultSet resultSet) throws SQLException {
			return new Milestone(resultSet.getInt("milestone_id"),
					resultSet.getInt("drive_id"),
					resultSet.getString("milestone"));
		}
	}
	
	public MilestoneDataAccess(String driverUrl) {
        super(driverUrl, new MilestoneMapper());
    }
	
	public Milestone addMilestone(int driveId, String milestone) {
		int milestoneId = insert("INSERT INTO drive_milestone (drive_id, order, stop) VALUES(?,?,?");
		return new Milestone(milestoneId, driveId, milestone);
	}
	
	public Milestone updateMilestone(int milestoneId, String milestone) {
		execute("UPDATE drive_milestone SET milestone = ? WHERE milestoneId = ?", milestone, milestoneId);
		return getMilestone(milestoneId);
	}
	
	public Milestone getMilestone(int milestoneId) {
		return queryFirst("SELECT milestone_id, drive_id, milestone WHERE milestone_id = ?", milestoneId);
	}
	
	public List<Milestone> getMilestoneForDrive(int driveId) {
		return query("SELECT milestone_id, drive_id, milestone WHERE drive_id = ?", driveId);
	}
	
	public boolean deleteMilestone(int milestoneId) {
		return execute("DELETE FROM drive_milestone WHERE milestone_id = ?", milestoneId) > 0;
	}
}
