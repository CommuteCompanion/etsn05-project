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

    public DriveWrap(Drive drive, List<DriveMilestone> milestones, List<DriveUser> users, List<DriveReport> reports) {
    	this.drive = drive;
    	this.milestones = milestones;
    	this.users = users;
    	this.reports = reports;
    }
    
    public Drive getDrive() {
    	return drive;
    }
    
    public List<DriveMilestone> getMilestones() {
    	return milestones;
    }
    
    public List<DriveUser> getUsers() {
    	return users;
    }
    
    public List<DriveReport> getReports() {
    	return reports;
    }
}


