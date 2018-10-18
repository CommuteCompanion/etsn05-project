package se.lth.base.server;

/**
 * Contains configuration for server settings.
 * In a "real" application this would be a more sophisticated solution, with support for reading/writing parameters
 * and error checking. The constructor approach is also not very nice when there are 10+ parameters.
 *
 * @author Group 1 ETSN05 2018
 */
public class Config {

	private static final int DEFAULT_PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
    private static final String DEFAULT_DATABASE_DRIVER = "jdbc:h2:tcp://localhost/~/base-server/data";

    private static final Config INSTANCE = new Config(
            DEFAULT_PORT, DEFAULT_DATABASE_DRIVER
    );

    private String databaseDriver;
    private int port;

    private Config(int port, String databaseDriver) {
        this.port = port;
        this.databaseDriver = databaseDriver;
    }

    public static Config instance() {
        return INSTANCE;
    }

    int getPort() {
        return port;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }
}
