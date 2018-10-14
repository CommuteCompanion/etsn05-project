package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("drive")
public class DriveResource {
	private final boolean IS_DRIVER = true;
	private final boolean IS_ACCEPTED = true;
    private final boolean IS_RATED = false;

    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
    private final DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
    private final DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
    private final DriveReportDataAccess driveReportDao = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
    private final User user;

    public DriveResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public DriveWrap createDrive(DriveWrap driveWrap) {
    	Drive drive = driveWrap.getDrive();
    	List<DriveMilestone> milestones = driveWrap.getMilestones();
    	
    	// Add drive
        drive = driveDao.addDrive(drive);

        List<DriveMilestone> returningMilestones = new ArrayList<>();
        
        // Add all milestones
        for (DriveMilestone m : milestones)
        	returningMilestones.add(driveMilestoneDao.addMilestone(drive.getDriveId(), m.getMilestone(), m.getDepartureTime()));
        
        // Add driver to list of users
        List<DriveUser> users = new ArrayList<>();
        users.add(driveUserDao.addDriveUser(drive.getDriveId(), user.getId(), drive.getStart(), drive.getStop(), IS_DRIVER, IS_ACCEPTED, IS_RATED));

        // No reports yet
        List<DriveReport> reports = new ArrayList<>();
        
        return new DriveWrap(drive, milestones, users, reports);
    }
    
    @Path("{driveId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Drive putDrive(@PathParam("driveId") int driveId, Drive drive) throws URISyntaxException {
    	if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver())
    		return driveDao.updateDrive(drive);

    	throw new WebApplicationException("Only driver allowed to update drive", Status.UNAUTHORIZED);
    }
    
    @Path("{driveId}")
    @GET
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveWrap getDrive(@PathParam("driveId") int driveId) throws URISyntaxException {
    	Drive drive = driveDao.getDrive(driveId);
    	List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
    	List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
    	List<DriveReport> reports = user.getRole().clearanceFor(Role.ADMIN) ? driveReportDao.getDriveReportsForDrive(driveId) : new ArrayList<DriveReport>();
    	
    	return new DriveWrap(drive, milestones, users, reports);
    }
    
    @Path("all")
    @GET
    @RolesAllowed(Role.Names.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Drive> getDrives() {
        return driveDao.getDrives();
    }

    @Path("{driveId}")
    @DELETE
    @RolesAllowed(Role.Names.USER)
    public void deleteDrive(@PathParam("driveId") int driveId) {
    	if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver())
    		driveDao.deleteDrive(driveId);
    	
    	throw new WebApplicationException("Only driver allowed to delete drive", Status.UNAUTHORIZED);
    }
    
    @Path("{driveId}/user")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveUser addUserToDrive(@PathParam("driveId") int driveId, DriveUser driveUser) {
    	if (driveDao.getDrive(driveId).getCarNumberOfSeats() > driveUserDao.getNumberOfUsersInDrive(driveId))
            return driveUserDao.addDriveUser(driveId, user.getId(), driveUser.getStart(), driveUser.getStop(), !IS_DRIVER, !IS_ACCEPTED, IS_RATED);
    	
    	throw new WebApplicationException("No available seats left", Status.PRECONDITION_FAILED);
    }
    
    @Path("{driveId}/user/{userId}")
    @PUT
    @RolesAllowed(Role.Names.USER)
    public DriveUser acceptUserInDrive(@PathParam("driveId") int driveId, @PathParam("userId") int userId) {
    	if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {
    		driveUserDao.acceptDriveUser(driveId, userId);
    		return driveUserDao.getDriveUser(driveId, userId);	
    	}
    	
    	throw new WebApplicationException("Only driver allowed to accept passengers", Status.UNAUTHORIZED);
    }
    
    @Path("{driveId}/user/{userId}")
    @DELETE
    @RolesAllowed(Role.Names.USER)
    public void removeUserFromDrive(@PathParam("driveId") int driveId, @PathParam("userId") int userId) {
    	if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver())
    		driveUserDao.deleteDriveUser(driveId, userId);
    	
    	throw new WebApplicationException("Only driver allowed to remove passengers", Status.UNAUTHORIZED);
    }
    
    @Path("{driveId}/rate")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void rateUsers(@PathParam("driveId") int driveId) {
    	if (!driveUserDao.getDriveUser(driveId, user.getId()).hasRated()) {
    		// Need a UserRate and updated User data object for this, doing this later
    		// Should return a UserRate object
    		// Should add a method in DriveUserAccess to has rated
    	}
    	
    	throw new WebApplicationException("You have already rated", Status.UNAUTHORIZED);
    }

    @Path("{driveId}/report")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveReport reportDrive(@PathParam("driveId") int driveId, DriveReport driveReport) {
    	return driveReportDao.addDriveReport(driveId, user.getId(), driveReport.getReportMessage());
    }

    @Path("all-reports")
    @GET
    @RolesAllowed(Role.Names.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<DriveWrap> getReportedDrives(){
    	List<DriveWrap> driveWraps = new ArrayList<DriveWrap>();
    	List<Drive> reportedDrives = driveDao.getReportedDrives();
    	
    	for(Drive d : reportedDrives) {
    		List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(d.getDriveId());
    		List<DriveUser> users = driveUserDao.getDriveUsersForDrive(d.getDriveId());
    		List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(d.getDriveId());
    		driveWraps.add(new DriveWrap(d, milestones, users, reports));
    	}
    	return driveWraps;
    }

    @Path("user/{userId}")
    @GET
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Drive> getDrivesForUser(@PathParam("userId") int userId) {
        if (userId == user.getId() || user.getRole().clearanceFor(Role.ADMIN)) {
            return driveDao.getDrivesForUser(userId);
        }
        throw new WebApplicationException("You do not have access to these drives", Status.UNAUTHORIZED);
    }
}
