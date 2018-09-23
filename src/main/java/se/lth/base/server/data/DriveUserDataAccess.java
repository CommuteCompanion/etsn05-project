package se.lth.base.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

/**
 * Data access class for a drive user
 * 
 * @author Group 1 ETSN05 2018
 *
 */
public class DriveUserDataAccess extends DataAccess<DriveUser> {
	public static final boolean IS_ACCEPTED = true;
	
	private static final class DriveMapper implements Mapper<DriveUser> {
        @Override
        public DriveUser map(ResultSet resultSet) throws SQLException {
            return new DriveUser(resultSet.getInt("drive_id"),
            		resultSet.getInt("user_id"),
            		resultSet.getString("start"),
            		resultSet.getString("stop"),
            		resultSet.getBoolean("driver"),
            		resultSet.getBoolean("accepted"));
        }
    }

    public DriveUserDataAccess(String driverUrl) {
        super(driverUrl, new DriveMapper());
    }
    
    public DriveUser addDriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted) {
    	insert("INSERT INTO drive_user (drive_id, user_id, start, stop, driver, accepted) VALUES (?,?,?,?,?,?)",
                driveId, userId, start, stop, driver, accepted);
    	
    	return new DriveUser(driveId, userId, start, stop, driver, accepted);
    }
    
    public DriveUser updateDriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted) {
    	execute("UPDATE drive_user SET start = ?, stop = ?, driver = ?, accepted = ? WHERE drive_id = ? AND user_id = ?",
                start, stop, driver, accepted, driveId, userId);
    	
    	return getDriveUser(driveId, userId);
    }
    
    public DriveUser getDriveUser(int driveId, int userId) {
    	return queryFirst("SELECT drive_id, user_id, start, stop, driver, accepted FROM drive_user WHERE drive_id = ? AND user_id = ?", 
    			driveId, userId);
    }
    
    public List<DriveUser> getDriveUsersForDrive(int driveId) {
    	return query("SELECT drive_id, user_id, start, stop, driver, accepted FROM drive_user WHERE drive_id = ?", driveId);
    }
    
    public boolean deleteDriveUser(int driveId, int userId) {
        return execute("DELETE FROM drive_user WHERE drive_id = ? AND user_id = ?", driveId, userId) > 0;
    }
    
    public boolean acceptDriveUser(int driveId, int userId) {
    	return execute("UPDATE drive_user SET accepted = ? WHERE drive_id = ? AND user_id = ?", IS_ACCEPTED, driveId, userId) > 0;
    }
}



