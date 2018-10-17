package se.lth.base.server.data;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class SearchFilterDataAccess extends DataAccess<SearchFilter> {

    public SearchFilterDataAccess(String driverUrl) {
        super(driverUrl, new SearchFilterMapper());
    }

    public SearchFilter addSearchFilter(SearchFilter searchFilter) {
        int searchFilterId = insert("INSERT INTO search_filter (user_id, start, stop, departure_time) VALUES(?,?,?,?)", searchFilter.getUserId(), searchFilter.getStart(), searchFilter.getStop(), new Timestamp(searchFilter.getDepartureTime()));
        return new SearchFilter(searchFilterId, searchFilter.getUserId(), searchFilter.getStart(), searchFilter.getStop(), searchFilter.getDepartureTime());
    }

    List<SearchFilter> getSearchFiltersForUser(int userId) {
        return query("SELECT * FROM search_filter WHERE user_id = ?", userId);
    }

    public List<SearchFilter> getSearchFilters() {
        return query("SELECT * FROM search_filter");
    }

    boolean deleteSearchFilter(int searchFilterId) {
        return execute("DELETE FROM search_filter WHERE search_filter_id = ?", searchFilterId) > 0;
    }

    private static final class SearchFilterMapper implements Mapper<SearchFilter> {
        @Override
        public SearchFilter map(ResultSet resultSet) throws SQLException {
            return new SearchFilter(resultSet.getInt("search_filter_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("start"),
                    resultSet.getString("stop"),
                    resultSet.getObject("departure_time", Timestamp.class).getTime());
        }
    }

}
