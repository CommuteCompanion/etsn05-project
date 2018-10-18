package se.lth.base.server.database;

import org.junit.After;
import org.junit.Before;
import se.lth.base.server.Config;
import se.lth.base.server.data.Role;
import se.lth.base.server.data.User;

import java.sql.Date;

/**
 * Base class for H2 database tests. The connection url configures an in-memory database.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public abstract class BaseDataAccessTest {
    protected static final User ADMIN = new User(1, Role.ADMIN, "admin@commutecompanion.se", "Admin",
            "-", "0", 0, Date.valueOf("2018-01-01").getTime(), false, 0, 0, 0);
    protected static final User TEST = new User(2, Role.USER, "commutecompaniontest@gmail.com", "Test",
            "-", "0", 0, Date.valueOf("2018-01-01").getTime(), false, 0, 0, 0);
    private static final String IN_MEM_DRIVER_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    static {
        Config.instance().setDatabaseDriver(IN_MEM_DRIVER_URL);
    }

    @Before
    public void createDatabase() {
        new CreateSchema(IN_MEM_DRIVER_URL).createSchema();
    }

    @After
    public void deleteDatabase() {
        new CreateSchema(IN_MEM_DRIVER_URL).dropAll();
    }
}
