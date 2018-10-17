package se.lth.base.server.data;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.ErrorType;
import se.lth.base.server.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access class for a drive user
 *
 * @author Group 1 ETSN05 2018
 */
public class DriveUserDataAccess extends DataAccess<DriveUser> {
    private static final boolean IS_ACCEPTED = true;
    private static final boolean HAS_RATED = true;
    private static final boolean IS_DRIVER = true;

    private static final class DriveMapper implements Mapper<DriveUser> {
        @Override
        public DriveUser map(ResultSet resultSet) throws SQLException {
            return new DriveUser(resultSet.getInt("drive_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("start"),
                    resultSet.getString("stop"),
                    resultSet.getBoolean("is_driver"),
                    resultSet.getBoolean("accepted"),
                    resultSet.getBoolean("rated"));
        }
    }

    public DriveUserDataAccess(String driverUrl) {
        super(driverUrl, new DriveMapper());
    }

    public DriveUser addDriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted, boolean rated) {
        execute("INSERT INTO drive_user (drive_id, user_id, start, stop, is_driver, accepted, rated) VALUES (?,?,?,?,?,?,?)",
                driveId, userId, start, stop, driver, accepted, rated);

        return new DriveUser(driveId, userId, start, stop, driver, accepted, rated);
    }

    public void updateDriveUser(int driveId, int userId, String start, String stop, boolean driver, boolean accepted, boolean rated) {
        execute("UPDATE drive_user SET start = ?, stop = ?, is_driver = ?, accepted = ?, rated = ? WHERE drive_id = ? AND user_id = ?",
                start, stop, driver, accepted, rated, driveId, userId);
    }

    public void hasRated(int userId, int driveId) {
        execute("UPDATE drive_user SET rated = ? WHERE user_id = ? AND drive_id = ?", HAS_RATED, userId, driveId);
    }

    public DriveUser getDriveUser(int driveId, int userId) {
        return queryFirst("SELECT drive_id, user_id, start, stop, is_driver, accepted, rated FROM drive_user WHERE drive_id = ? AND user_id = ?",
                driveId, userId);
    }

    public List<DriveUser> getDriveUsersForDrive(int driveId) {
        return query("SELECT drive_id, user_id, start, stop, is_driver, accepted, rated FROM drive_user WHERE drive_id = ?", driveId);
    }

    public void deleteDriveUser(int driveId, int userId) {
        execute("DELETE FROM drive_user WHERE drive_id = ? AND user_id = ?", driveId, userId);
    }

    public void acceptDriveUser(int driveId, int userId) {
        execute("UPDATE drive_user SET accepted = ? WHERE drive_id = ? AND user_id = ?", IS_ACCEPTED, driveId, userId);
    }

    public int getNumberOfUsersInDrive(int driveId) {
        ResultSet result = openQuery("SELECT COUNT (*) FROM drive_user WHERE drive_id = ? AND accepted = ?", driveId, IS_ACCEPTED);

        try {
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException(ErrorType.UNKNOWN);
        }
    }

    public int getNumberOfDrivesForUser(int userId) {
        ResultSet result = openQuery("SELECT COUNT(*) FROM drive_user INNER JOIN drive ON drive_user.drive_id = drive.drive_id WHERE drive_user.is_driver = ? " +
                "AND drive_user.user_id = ? AND drive.drive_id = drive_user.drive_id " +
                "AND drive.arrival_time < CURRENT_TIMESTAMP()", IS_DRIVER, userId);

        try {
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException(ErrorType.UNKNOWN);
        }
    }
}



