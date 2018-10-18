package se.lth.base.server.data;

import java.util.List;

/**
 * Data class for a drive
 *
 * @author Group 1 ETSN05 2018
 * @see DriveDataAccess
 */
public class DriveWrap {
    private final Drive drive;
    private final List<DriveMilestone> milestones;
    private final List<DriveUser> users;
    private final List<DriveReport> reports;

    /**
     * Holds information about a drive.
     *
     * @param drive      a Drive object containing information about start, stop, departure time, etc.
     * @param milestones a list of milestones for the drive.
     * @param users      a list of users in the drive.
     * @param reports    a list of reports.
     */
    public DriveWrap(Drive drive, List<DriveMilestone> milestones, List<DriveUser> users, List<DriveReport> reports) {
        this.drive = drive;
        this.milestones = milestones;
        this.users = users;
        this.reports = reports;
    }

    /** @return a Drive object. */
    public Drive getDrive() {
        return drive;
    }

    /** @return a list of milestones for the drive. */
    public List<DriveMilestone> getMilestones() {
    	return milestones;
    }

    /** @return a list of users in the drive. */
    public List<DriveUser> getUsers() {
        return users;
    }

    /** @return a list of reports for the drive. */
    public List<DriveReport> getReports() {
    	return reports;
    }
}


