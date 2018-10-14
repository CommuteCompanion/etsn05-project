package se.lth.base.server.data;

/**
 * Data class for a drive
 *
 * @author Group 1 ETSN05 2018
 * @see DriveDataAccess
 */
public class Drive {
    private final int driveId;
    private final int carNumberOfSeats;
    private final int optLuggageSize;
    private final String start;
    private final String stop, comment;
    private final String carBrand;
    private final String carModel;
    private final String carColor;
    private final String carLicensePlate;
    private final boolean optWinterTires;
    private final boolean optPets;
    private final boolean optBicycle;
    private final long departureTime;
    private final long arrivalTime;

    public Drive(int driveId, String start, String stop, long departureTime, long arrivalTime, String comment, String carBrand,
                 String carModel, String carColor, String carLicensePlate, int carNumberOfSeats, int optLuggageSize,
                 boolean optWinterTires, boolean optBicycle, boolean optPets) {
        this.driveId = driveId;
        this.start = start;
        this.stop = stop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.comment = comment;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carColor = carColor;
        this.carLicensePlate = carLicensePlate;
        this.carNumberOfSeats = carNumberOfSeats;
        this.optLuggageSize = optLuggageSize;
        this.optWinterTires = optWinterTires;
        this.optBicycle = optBicycle;
        this.optPets = optPets;
    }

    public int getDriveId() {
        return driveId;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
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

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public int getCarNumberOfSeats() {
        return carNumberOfSeats;
    }

    public int getOptLuggageSize() {
        return optLuggageSize;
    }

    public boolean getOptWinterTires() {
        return optWinterTires;
    }

    public boolean getOptBicycle() {
        return optBicycle;
    }

    public boolean getOptPets() {
        return optPets;
    }
}


