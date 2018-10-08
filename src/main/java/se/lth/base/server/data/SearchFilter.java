package se.lth.base.server.data;

import java.sql.Timestamp;

public class SearchFilter {

    private final int searchFilterId;
    private final int userId;
    private final String start;
    private final String stop;
    private final Timestamp departureTime;

    public SearchFilter(int searchFilterId, int userId, String start, String stop, Timestamp departureTime) {
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

    public Timestamp getDepartureTime() {
        return departureTime;
    }
}
