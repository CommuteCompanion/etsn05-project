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
                    resultSet.getString("car_license_plate"),
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
    
    public Drive addDrive(int userId, String start, String stop, long dateTime, String comment, 
    		String carBrand, String carModel, int carYear, String carColor, String carLicensePlate, int carNumberOfSeats,
    		boolean optLuggage, boolean optWinterTires, boolean optPets, boolean optBicycle, boolean optSkis) {
    	long created = System.currentTimeMillis();

    	int driveId = insert("INSERT INTO drive (user_id, start, stop, date_time, comment, car_brand, car_model, car_year, car_color, car_license_plate, car_number_of_seats, opt_luggage, opt_winter_tires, opt_pets, opt_bicycles, opt_skis) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                userId, start, stop, new Date(dateTime), comment, carBrand, carModel, carYear, carColor, carLicensePlate, 
                carNumberOfSeats, optLuggage, optWinterTires, optPets, optBicycle, optSkis, new Date(created));
    	
    	return new Drive(driveId, userId, start, stop, dateTime, comment, carBrand, carModel, carYear, carColor, carLicensePlate, 
                carNumberOfSeats, optLuggage, optWinterTires, optPets, optBicycle, optSkis, created);
    }
}



