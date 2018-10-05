package se.lth.base.server.rest;

import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.Config;
import se.lth.base.server.data.Drive;
import se.lth.base.server.data.UserDataAccess;

import javax.ws.rs.core.GenericType;
import java.util.List;


public class DriveResourceTest extends BaseResourceTest {

    private static final GenericType<List<Drive>> DRIVE_LIST = new GenericType<List<Drive>>() {
    };


    @Test
    public void getDrivesForUser() {
        login(TEST_CREDENTIALS);

        UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());
        int userId = userDao.getUserByUserName(TEST_CREDENTIALS.getUsername()).getId();

        target("drive")
                .path("user/2")
                .request()
                .get(DRIVE_LIST);
    }
}

