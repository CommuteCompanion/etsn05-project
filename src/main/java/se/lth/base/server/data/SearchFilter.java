package se.lth.base.server.data;

import java.sql.Timestamp;

public class SearchFilter {

    private final int searchFilterId, userId;
    private String start, stop;
    private Timestamp departureTime;

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

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }
}
