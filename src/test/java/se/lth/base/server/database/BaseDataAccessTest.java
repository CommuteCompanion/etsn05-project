package se.lth.base.server.database;

import org.junit.After;
import org.junit.Before;
import se.lth.base.server.Config;
import se.lth.base.server.data.Credentials;
import se.lth.base.server.data.Role;
import se.lth.base.server.data.User;

import java.sql.Date;
import java.sql.SQLException;

/**
 * Base class for H2 database tests. The connection url configures an in-memory database.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public abstract class BaseDataAccessTest {
    protected static final User ADMIN = new User(1, Role.ADMIN, "admin@commutecompanion.se", "Admin",
            "-", "0", 0, Date.valueOf("2018-01-01").getTime(), false, 0, 0, 0);
    protected static final Credentials ADMIN_CREDENTIALS = new Credentials("admin@commutecompanion.se",
            "admin123", Role.ADMIN, ADMIN);
    protected static final User TEST = new User(2, Role.USER, "commutecompaniontest@gmail.com", "Test",
            "-", "0", 0, Date.valueOf("2018-01-01").getTime(), false, 0, 0, 0);
    protected static final Credentials TEST_CREDENTIALS = new Credentials("commutecompaniontest@gmail.com",
            "password", Role.USER, TEST);
    private static final String IN_MEM_DRIVER_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    static {
        Config.instance().setDatabaseDriver(IN_MEM_DRIVER_URL);
    }

    @Before
    public void createDatabase() throws SQLException {
        new CreateSchema(IN_MEM_DRIVER_URL).createSchema();
    }

    @After
    public void deleteDatabase() throws SQLException {
        new CreateSchema(IN_MEM_DRIVER_URL).dropAll();
    }
}
