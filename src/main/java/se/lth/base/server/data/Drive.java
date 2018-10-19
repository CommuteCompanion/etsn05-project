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
    private final String stop;
    private final String comment;
    private final String carBrand;
    private final String carModel;
    private final String carColor;
    private final String carLicensePlate;
    private final boolean optWinterTires;
    private final boolean optPets;
    private final boolean optBicycle;
    private final long departureTime;
    private final long arrivalTime;

    /**
     * @param driveId          the Id of the drive.
     * @param start            the start location of the drive.
     * @param stop             the final destination of the drive.
     * @param departureTime    the time and date of the departure.
     * @param arrivalTime      the time and date of arrival
     * @param comment          additional comment, could be used when extra information about a drive is needed.
     * @param carBrand         the brand of the car.
     * @param carModel         the model of the car.
     * @param carColor         the color of the car.
     * @param carLicensePlate  the license plate of the car.
     * @param carNumberOfSeats the number of seats in the car.
     * @param optLuggageSize   the size of the luggage in the car. Can take values 0,1 or 2 (referring to small, medium and large).
     * @param optWinterTires   specifies if the car has winter tiers or not.
     * @param optBicycle       specifies if it is possible to bring a bicycle.
     * @param optPets          specifies if pets are allowed in the car.
     */
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

    /**
     * Returns the driveId.
     */
    public int getDriveId() {
        return driveId;
    }

    /**
     * Returns the start location.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the stop location.
     */
    public String getStop() {
        return stop;
    }

    /**
     * Returns the departure time.
     */
    public long getDepartureTime() {
        return departureTime;
    }

    /**
     * Returns the arrival time.
     */
    public long getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Returns the comment of a drive.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the brand of the car.
     */
    String getCarBrand() {
        return carBrand;
    }

    /**
     * Returns the model of the car.
     */
    String getCarModel() {
        return carModel;
    }

    /**
     * Returns the color of the car.
     */
    public String getCarColor() {
        return carColor;
    }

    /**
     * Returns the license plate of the car.
     */
    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    /**
     * Returns the number of seats in the car.
     */
    public int getCarNumberOfSeats() {
        return carNumberOfSeats;
    }

    /**
     * Returns the size of the luggage in the car.
     */
    int getOptLuggageSize() {
        return optLuggageSize;
    }

    /**
     * Returns true if the car has winter tires, otherwise false.
     */
    boolean getOptWinterTires() {
        return optWinterTires;
    }

    /**
     * Returns true if it is possible to bring a bicycle, otherwise false.
     */
    boolean getOptBicycle() {
        return optBicycle;
    }

    /**
     * Returns true if pets are allowed in the car, otherwise false.
     */
    boolean getOptPets() {
        return optPets;
    }
}


