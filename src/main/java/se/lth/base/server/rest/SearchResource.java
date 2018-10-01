package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.Iterator;
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

        // TODO Get requested trip start, stop and departure
        String tripStart = "";
        String tripStop = "";
        String departure = "";


        // Get all drives matching start and end point of search
        List<Drive> filteredDrives = filterDrivesMatchingTrip(tripStart, tripStop);


        // TODO
        return null;
    }

    // TODO Add RESTful tags
    public List<User> getUsers() {
        // TODO
        return null;
    }

    private List<Drive> filterDrivesMatchingTrip(String tripStart, String tripStop) {
        // TODO Time-matching issue? (Depature time for all milestones, not only the drive)


        List<Drive> drives = driveDao.getDrives();
        Iterator<Drive> iterator = drives.iterator();
        while (iterator.hasNext()) {
            Drive drive = iterator.next();

            // TODO Get all milestones in order (including start and end of trip) with the fields: name, departure
            // TODO Get all trip associated with drive and create interval objects containing: startIndex, stopIndex, departure
            // TODO Check so that departure time of new passenger is possible without delaying the other existing passengers too much
            // TODO Check so that car does not get full adding the new passenger


            /*
            drive.getD
            List<String> allDrivePoints = new ArrayList<>();
            allDrivePoints.add(drive.getStart());
            allDrivePoints.add(drive.getStop());
            List<DriveMilestone> driveMilestones = driveMilestoneDao.getMilestonesForDrive();
            for(DriveMilestone driveMilestone : driveMilestones) {
                allDrivePoints.add(driveMilestone.)
            }
            */
        }

        return null;
    }
}
