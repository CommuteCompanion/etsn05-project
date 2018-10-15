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

	public DriveMilestone addMilestone(int driveId, String milestone, long departureTime) {
        int milestoneId = insert("INSERT INTO drive_milestone (drive_id, milestone_name, departure_time) VALUES(?,?,?)", driveId, milestone, new Timestamp(departureTime));
		return new DriveMilestone(milestoneId, driveId, milestone, departureTime);
	}

    public DriveMilestoneDataAccess(String driverUrl) {
        super(driverUrl, new MilestoneMapper());
    }

    public DriveMilestone updateMilestone(int milestoneId, String milestone, long departureTime) {
        execute("UPDATE drive_milestone SET milestone_name = ?, departure_time = ? WHERE milestone_id = ?", milestone, new Timestamp(departureTime), milestoneId);
		return getMilestone(milestoneId);
	}

	public DriveMilestone getMilestone(int milestoneId) {
        return queryFirst("SELECT milestone_id, drive_id, milestone_name, departure_time FROM drive_milestone WHERE milestone_id = ?", milestoneId);
	}

	public List<DriveMilestone> getMilestonesForDrive(int driveId) {
        return query("SELECT milestone_id, drive_id, milestone_name, departure_time FROM drive_milestone WHERE drive_id = ?", driveId);
    }

    public boolean deleteMilestone(int milestoneId) {
        return execute("DELETE FROM drive_milestone WHERE milestone_id = ?", milestoneId) > 0;
    }

    private static final class MilestoneMapper implements Mapper<DriveMilestone> {
        @Override
        public DriveMilestone map(ResultSet resultSet) throws SQLException {
            return new DriveMilestone(resultSet.getInt("milestone_id"),
                    resultSet.getInt("drive_id"),
                    resultSet.getString("milestone_name"),
                    resultSet.getObject("departure_time", Timestamp.class).getTime());
        }
    }
}
