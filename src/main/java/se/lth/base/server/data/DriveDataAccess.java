package se.lth.base.server.data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

public class DriveDataAccess extends DataAccess<Drive> {
	private static final class DriveMapper implements Mapper<Drive> {
        @Override
        public Drive map(ResultSet resultSet) throws SQLException {
            return new Drive(resultSet.getInt("drive_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("start"),
                    resultSet.getString("stop"),
                    resultSet.getObject("date_time", Date.class).getTime(),
                    resultSet.getString("comment"),
                    resultSet.getString("car_brand"),
                    resultSet.getString("car_model"),
                    resultSet.getInt("car_year"),
                    resultSet.getString("car_color"),
                    resultSet.getString("car_lincese_plate"),
                    resultSet.getInt("car_number_of_seats"),
                    resultSet.getBoolean("opt_luggage"),
                    resultSet.getBoolean("opt_winter_tires"),
                    resultSet.getBoolean("opt_pets"),
                    resultSet.getBoolean("opt_bicycle"),
                    resultSet.getBoolean("opt_skis"),
                    resultSet.getObject("created", Date.class).getTime());
        }
    }

    public DriveDataAccess(String driverUrl) {
        super(driverUrl, new DriveMapper());
    }
}



