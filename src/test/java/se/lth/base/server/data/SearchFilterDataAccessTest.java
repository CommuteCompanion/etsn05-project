package se.lth.base.server.data;

import org.junit.Assert;
import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;

import java.sql.Timestamp;
import java.util.List;

public class SearchFilterDataAccessTest extends BaseDataAccessTest {

    private SearchFilterDataAccess searchFilterDao = new SearchFilterDataAccess(Config.instance().getDatabaseDriver());

    @Test
    // Tests addSearchFilter(SearchFilter), deleteSearchFilter(int), getSearchFilters()
    public void addAndDeleteSearchFilter() {
        // Check for empty list
        List<SearchFilter> searchFilters = searchFilterDao.getSearchFilters();
        Assert.assertEquals(0, searchFilters.size());

        // Save search filter
        long departureTime = new Timestamp(2018, 10, 20, 12, 0, 0, 0).getTime();
        SearchFilter searchFilter = new SearchFilter(-1, TEST.getId(), "A", "B", departureTime);
        searchFilter = searchFilterDao.addSearchFilter(searchFilter);

        // Check for non-empty list
        searchFilters = searchFilterDao.getSearchFilters();
        Assert.assertEquals(1, searchFilters.size());

        // Verify attributes
        searchFilter = searchFilters.get(0);
        Assert.assertEquals(TEST.getId(), searchFilter.getUserId());
        Assert.assertEquals("A", searchFilter.getStart());
        Assert.assertEquals("B", searchFilter.getStop());
        Assert.assertEquals(departureTime, searchFilter.getDepartureTime());

        // Delete search filter
        Assert.assertTrue(searchFilterDao.deleteSearchFilter(searchFilter.getId()));

        // Check for empty list
        searchFilters = searchFilterDao.getSearchFilters();
        Assert.assertEquals(0, searchFilters.size());
    }

    @Test
    // Tests getSearchFilterForUser
    public void getSearchFilterForUser() {
        // Check for empty list
        List<SearchFilter> searchFiltersUser = searchFilterDao.getSearchFiltersForUser(TEST.getId());
        Assert.assertEquals(0, searchFiltersUser.size());

        // Create two search filters for two users
        long departureTime = new Timestamp(2018, 10, 20, 12, 0, 0, 0).getTime();
        SearchFilter searchFilter1 = new SearchFilter(-1, TEST.getId(), "A", "B", departureTime);
        searchFilter1 = searchFilterDao.addSearchFilter(searchFilter1);

        SearchFilter searchFilter2 = new SearchFilter(-1, ADMIN.getId(), "C", "D", departureTime);
        searchFilter2 = searchFilterDao.addSearchFilter(searchFilter2);

        // Check for non-empty list where start of SearchFilter is equal to "A"
        searchFiltersUser = searchFilterDao.getSearchFiltersForUser(TEST.getId());
        Assert.assertEquals(1, searchFiltersUser.size());
        Assert.assertEquals("A", searchFiltersUser.get(0).getStart());
    }
}
