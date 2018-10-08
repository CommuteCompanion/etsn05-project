package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.data.Drive;
import se.lth.base.server.data.SearchFilter;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class SearchResourceTest extends BaseResourceTest {

    private static final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>(){};

    @Test
    public void getDrivesMatching() {
        /*
        login(TEST_CREDENTIALS);

        target("drive")
                .path("")
                .request()
                .post()


        SearchFilter searchFilter = new SearchFilter(-1, -1, "")


        target("search")
                .path("getDrives")
                .request()
                .post()
        */

    }


}
