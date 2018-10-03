package se.lth.base.server.data;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

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
					resultSet.getString("milestone"),
					resultSet.getTimestamp("departure_time"));
		}
	}

	public DriveMilestoneDataAccess(String driverUrl) {
		super(driverUrl, new MilestoneMapper());
	}

	public DriveMilestone addMilestone(int driveId, String milestone, Timestamp departureTime) {
		int milestoneId = insert("INSERT INTO drive_milestone (drive_id, order, stop, departure_time) VALUES(?,?,?,?)");
		return new DriveMilestone(milestoneId, driveId, milestone, departureTime);
	}

	public DriveMilestone updateMilestone(int milestoneId, String milestone) {
		execute("UPDATE drive_milestone SET milestone = ? WHERE milestone_id = ?", milestone, milestoneId);
		return getMilestone(milestoneId);
	}

	public DriveMilestone getMilestone(int milestoneId) {
		return queryFirst("SELECT milestone_id, drive_id, milestone FROM drive_milestone WHERE milestone_id = ?", milestoneId);
	}

	public List<DriveMilestone> getMilestonesForDrive(int driveId) {
		return query("SELECT milestone_id, drive_id, milestone FROM drive_milestone WHERE drive_id = ?", driveId);
	}

	public boolean deleteMilestone(int milestoneId) {
		return execute("DELETE FROM drive_milestone WHERE milestone_id = ?", milestoneId) > 0;
	}
}
