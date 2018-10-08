package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.sql.Timestamp;
import java.util.ArrayList;
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
        // TODO Overall thought
        // Get all milestones in order (including start and end of trip) with the fields: name, departure
        // Get all trip associated with drive and create interval objects containing: startIndex, stopIndex, departure
        // Check so that departure time of new passenger is possible without delaying the other existing passengers too much
        // Check so that car does not get full adding the new passenger
        // Check all other requirements such as luggage etc.

        List<Drive> drives = driveDao.getDrives();
        Iterator<Drive> iterator = drives.iterator();
        while (iterator.hasNext()) {
            Drive drive = iterator.next();
            // Create drive stop and start as "DriveMilestones" and add the to the list
            // Milestone id is not of intreset and is therefore set to 0
            DriveMilestone driveStart = new DriveMilestone(0, drive.getDriveId(), drive.getStart(), drive.getDepartureTime());
            // TODO Does driveStop need a "arrival" time?
            DriveMilestone driveStop = new DriveMilestone(0, drive.getDriveId(), drive.getStop(), null);
            List<DriveMilestone> driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
            driveMilestones.add(0, driveStart);
            driveMilestones.add(driveStop);
            List<DriveUser> driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId());
            int carSeats = drive.getCarNumberOfSeats();

            // Check if too many drive users start and stop overlap (seats taken > max seats)
            if(checkMilestoneIntervalOverlap(driveMilestones, driveUsers, carSeats)) {
                iterator.remove();
                continue;
            }
        }
        return drives;
    }

    private boolean checkMilestoneIntervalOverlap(List<DriveMilestone> milestones, List<DriveUser> driveUsers, int carSeats) {
        // Create DriveUserIntervals
        List<DriveUserInterval> driveUserIntervals = new ArrayList<>();
        for(DriveUser driveUser : driveUsers) {
            // TODO departureTime is null
            driveUserIntervals.add(new DriveUserInterval(driveUser.getStart(), driveUser.getStop(), null, milestones));
        }

        for(int i = 0; i < milestones.size() - 1; i++) {
            // Seats taken in this interval
            int seatsTaken = 0;
            int start = i;
            int stop = i + 1;
            for(DriveUserInterval driveUserInterval : driveUserIntervals) {
                int startIndex = driveUserInterval.getStartIndex();
                int endIndex = driveUserInterval.getStopIndex();
                if(startIndex <= start && endIndex >= stop) {
                    seatsTaken++;
                }
            }
            if(seatsTaken > carSeats) {
                // The car will be too full at some interval if the new passenger is added!
                return true;
            }
        }
        return false;
    }

    private class DriveUserInterval {
        private int startIndex;
        private int stopIndex;
        private Timestamp departureTime;

        public DriveUserInterval(String start, String stop, Timestamp departureTime, List<DriveMilestone> milestones) {
            startIndex = getIndexOfMilestone(start, milestones);
            stopIndex = getIndexOfMilestone(stop, milestones);
            this.departureTime = departureTime;
        }

        private int getIndexOfMilestone(String name, List<DriveMilestone> milestones) {
            for(int i = 0; i < milestones.size(); i++) {
                if(milestones.get(i).getMilestone().equals(name)) {
                    return i;
                }
            }
            return -1;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getStopIndex() {
            return stopIndex;
        }

        public Timestamp getDepartureTime() {
            return departureTime;
        }
    }

}
