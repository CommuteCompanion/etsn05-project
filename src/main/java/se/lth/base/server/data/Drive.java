package se.lth.base.server.data;

/**
 * Data class for a Drive
 *
 * @author Group 1 ETSN05 2018
 * @see DriveDataAcess (not created yet)
 */
public class Drive {
    private final int driveId,
    	userId,
    	carYear,
    	carNumberOfSeats;
    private final String start,
    	stop,
    	comment,
    	carBrand,
    	carModel,
    	carColor,
    	carLicensePlate; 
    private final boolean optLuggage, 
    	optWinterTires, 
    	optPets, 
    	optBicycle, 
    	optSkis;
    private final long dateTime, created;

    public Drive(int driveId, int userId, String start, String stop, long dateTime, String comment, 
    		String carBrand, String carModel, int carYear, String carColor, String carLicensePlate, int carNumberOfSeats,
    		int optLuggage, int optWinterTires, int optPets, int optBicycle, int optSkis, long created) {
    	this.driveId = driveId;
    	this.userId = userId;
    	this.start = start;
    	this.stop = stop;
    	this.dateTime = dateTime;
    	this.comment = comment;
    	this.carBrand = carBrand;
    	this.carModel = carModel;
    	this.carYear = carYear;
    	this.carColor = carColor;
    	this.carLicensePlate = carLicensePlate;
    	this.carNumberOfSeats = carNumberOfSeats;
    	this.optLuggage = optLuggage != 0;
    	this.optWinterTires = optWinterTires != 0;
    	this.optPets = optPets != 0;
    	this.optBicycle = optBicycle != 0;
    	this.optSkis = optSkis != 0;
    	this.created = created;
    }

	public int getDriveId() {
		return driveId;
	}

	public int getUserId() {
		return userId;
	}

	public int getCarYear() {
		return carYear;
	}

	public int getCarNumberOfSeats() {
		return carNumberOfSeats;
	}

	public String getStart() {
		return start;
	}

	public String getStop() {
		return stop;
	}

	public String getComment() {
		return comment;
	}

	public String getCarBrand() {
		return carBrand;
	}

	public String getCarModel() {
		return carModel;
	}

	public String getCarColor() {
		return carColor;
	}

	public boolean isOptLuggage() {
		return optLuggage;
	}

	public String getCarLicensePlate() {
		return carLicensePlate;
	}

	public boolean getOptWinterTires() {
		return optWinterTires;
	}

	public boolean getOptPets() {
		return optPets;
	}

	public boolean getOptBicycle() {
		return optBicycle;
	}

	public boolean getOptSkis() {
		return optSkis;
	}

	public long getDateTime() {
		return dateTime;
	}

	public long getCreated() {
		return created;
	}

    
}


