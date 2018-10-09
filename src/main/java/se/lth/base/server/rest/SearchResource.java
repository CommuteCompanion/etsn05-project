package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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

    // TODO Maybe change this value
    // A trip with departure time within interval 13.00-13.10 will match drive milestone with departure time 13.10
    private final int SEARCH_MINUTES_MARGIN = 10;

    public SearchResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    /**
     * This method lets a user search for drives by specifying search parameters such as trip start, stop and departure time
     *
     * @param searchFilter is used to filter out possible drives. If the timestamp attribute in this object is null, then filtering will be done only on trip start and stop
     * @return A list of Drive-objects matching the input arguments
     */
    @Path("getDrives")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Drive> getDrives(SearchFilter searchFilter) {

        // Get requested trip start, stop and departure
        String tripStart = searchFilter.getStart();
        String tripStop = searchFilter.getStop();
        Timestamp departure = searchFilter.getDepartureTime();


        // Get all drives matching start and end point of search
        List<Drive> filteredDrives = filterDrivesMatchingTrip(tripStart, tripStop, departure);

        return filteredDrives;
    }

    // TODO Add RESTful tags
    public List<User> getUsers() {
        // TODO
        return null;
    }

    private List<Drive> filterDrivesMatchingTrip(String tripStart, String tripStop, Timestamp departureTime) {
        // Get all drives
        List<Drive> drives = driveDao.getDrives();
        Iterator<Drive> iterator = drives.iterator();

        // Loop through all drives
        while (iterator.hasNext()) {
            Drive drive = iterator.next();
            // Create drive stop and start as "DriveMilestones" and add the to the list
            // Milestone id is not of interest and is therefore set to -1
            DriveMilestone driveStart = new DriveMilestone(-1, drive.getDriveId(), drive.getStart(), drive.getDepartureTime());
            // TODO Does driveStop need a "arrival" time?
            DriveMilestone driveStop = new DriveMilestone(-1, drive.getDriveId(), drive.getStop(), null);
            List<DriveMilestone> driveMilestones = driveMilestoneDao.getMilestonesForDrive(drive.getDriveId());
            driveMilestones.add(0, driveStart);
            driveMilestones.add(driveStop);

            // Make sure that tripStart and tripStop exists in driveMilestones and that they are not the same and that tripStart is before tripStop
            if (!(doesTripStartExist(tripStart, driveMilestones) && doesTripStopExist(tripStop, driveMilestones) &&
                    !isTripStartSameAsTripStop(tripStart, tripStop) && isTripStartBeforeTripStop(tripStart, tripStop, driveMilestones))) {
                iterator.remove();
                continue;
            }

            // Get all DriveUsers associated with the current drive
            List<DriveUser> driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId());

            // Remove driver from driveUsers list
            Iterator<DriveUser> driveUsersIterator = driveUsers.iterator();
            while (driveUsersIterator.hasNext()) {
                DriveUser driveUser = driveUsersIterator.next();
                if (driveUser.isDriver()) {
                    driveUsersIterator.remove();
                    break;
                }
            }

            // Add the potentially new passenger to the driveUser list for this drive
            driveUsers.add(new DriveUser(-1, -1, tripStart, tripStop, false, false, false));
            int carSeats = drive.getCarNumberOfSeats();

            // Check if too many drive users start and stop overlap (seats taken > max seats)
            if(checkMilestoneIntervalOverlap(driveMilestones, driveUsers, carSeats)) {
                iterator.remove();
                continue;
            }

            // Check if so that departure time of passenger matches with departure time of milestone set by driver
            if (departureTime != null) {
                if (!checkDepartureTimeMatch(departureTime, tripStart, driveMilestones)) {
                    iterator.remove();
                    continue;
                }
            }

            // TODO Check so that passenger has no other bookings (passenger in another drive or driver in another drive)

        }
        return drives;
    }

    private boolean doesTripStartExist(String tripStart, List<DriveMilestone> driveMilestones) {
        for (DriveMilestone driveMilestone : driveMilestones) {
            if (driveMilestone.getMilestone().equals(tripStart)) return true;
        }
        return false;
    }

    private boolean doesTripStopExist(String tripStop, List<DriveMilestone> driveMilestones) {
        for (DriveMilestone driveMilestone : driveMilestones) {
            if (driveMilestone.getMilestone().equals(tripStop)) return true;
        }
        return false;
    }

    private boolean isTripStartSameAsTripStop(String tripStart, String tripStop) {
        return tripStart.equals(tripStop);
    }

    private boolean isTripStartBeforeTripStop(String tripStart, String tripStop, List<DriveMilestone> driveMilestones) {
        for (DriveMilestone driveMilestone : driveMilestones) {
            if (driveMilestone.getMilestone().equals(tripStart)) {
                return true;
            } else if (driveMilestone.getMilestone().equals(tripStop)) {
                return false;
            }
        }
        return false;
    }

    private boolean checkMilestoneIntervalOverlap(List<DriveMilestone> milestones, List<DriveUser> driveUsers, int carSeats) {
        // Create DriveUserIntervals
        List<DriveUserInterval> driveUserIntervals = new ArrayList<>();
        for(DriveUser driveUser : driveUsers) {
            driveUserIntervals.add(new DriveUserInterval(driveUser.getStart(), driveUser.getStop(), milestones));
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

    private boolean checkDepartureTimeMatch(Timestamp departureTime, String startMiletone, List<DriveMilestone> driveMilestones) {
        // Find the departure time of the milestone
        for (DriveMilestone driveMilestone : driveMilestones) {
            if (driveMilestone.getMilestone().equals(startMiletone)) {
                Timestamp milestoneDepartureTime = driveMilestone.getDepartureTime();

                Timestamp timeMargin = new Timestamp(milestoneDepartureTime.getTime() - SEARCH_MINUTES_MARGIN * 60 * 1000);
                if ((timeMargin.before(departureTime) || timeMargin.equals(departureTime)) &&
                        (departureTime.before(milestoneDepartureTime) || departureTime.equals(milestoneDepartureTime))) {
                    return true;
                }
            }
        }
        return false;
    }

    private class DriveUserInterval {
        private int startIndex;
        private int stopIndex;

        public DriveUserInterval(String start, String stop, List<DriveMilestone> milestones) {
            startIndex = getIndexOfMilestone(start, milestones);
            stopIndex = getIndexOfMilestone(stop, milestones);
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
    }

}
