package se.lth.base.server.data;

public class SearchFilter {

    private final int searchFilterId;
    private final int userId;
    private final String start;
    private final String stop;
    private final long departureTime;

    /**
     * Creates a simple search filter object
     *
     * @param searchFilterId The unique id of this search filter, generated by the database. Can be left as -1 if coming from front-end.
     * @param userId         The user id of the user making the request.
     * @param start          The name of the trip start location to search for.
     * @param stop           The name of the trip stop location to search for.
     * @param departureTime  this parameter should be equal to -1 if one wishes to not use this parameter
     */
    public SearchFilter(int searchFilterId, int userId, String start, String stop, long departureTime) {
        this.searchFilterId = searchFilterId;
        this.userId = userId;
        this.start = start;
        this.stop = stop;
        this.departureTime = departureTime;
    }

    public int getId() {
        return searchFilterId;
    }

    public int getUserId() {
        return userId;
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
}
