package se.lth.base.server.rest.providers;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.List;

@Path("search")
public class SearchResource {

    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
    private final DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
    private final DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
    private final DriveReportDataAccess driveReportDao = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
    private final User user;

    public SearchResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    // TODO Add RESTful tags
    public List<Drive> getDrives() {
        // TODO
        return null;
    }

    // TODO Add RESTful tags
    public List<User> getUsers() {
        // TODO
        return null;
    }

}
