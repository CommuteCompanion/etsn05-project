package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.Drive;
import se.lth.base.server.data.DriveDataAccess;
import se.lth.base.server.data.DriveMilestone;
import se.lth.base.server.data.DriveMilestoneDataAccess;
import se.lth.base.server.data.DriveReport;
import se.lth.base.server.data.DriveReportDataAccess;
import se.lth.base.server.data.DriveUser;
import se.lth.base.server.data.DriveUserDataAccess;
import se.lth.base.server.data.DriveWrap;
import se.lth.base.server.data.Role;
import se.lth.base.server.data.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("drive")
public class DriveResource {
	private final boolean IS_DRIVER = true;
	private final boolean IS_ACCEPTED = true;
	
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
    public DriveWrap createDrive(DriveWrap driveWrap) throws URISyntaxException {
    	Drive drive = driveWrap.getDrive();
    	List<DriveMilestone> milestones = driveWrap.getMilestones();
    	
    	// Add drive
        drive = driveDao.addDrive(user.getId(), drive.getStart(), drive.getStop(), drive.getDateTime(), drive.getComment(), drive.getCarBrand(), drive.getCarModel(), drive.getCarYear(), drive.getCarColor(), drive.getCarLicensePlate(), drive.getCarNumberOfSeats(), drive.getOptLuggage(), drive.getOptWinterTires(), drive.getOptPets(), drive.getOptBicycle(), drive.getOptSkis());
        
        List<DriveMilestone> returningMilestones = new ArrayList<DriveMilestone>();
        
        // Add all milestones
        for (DriveMilestone m : milestones)
        	returningMilestones.add(driveMilestoneDao.addMilestone(drive.getDriveId(), m.getMilestone()));
        
        // Add driver to list of users
        List<DriveUser> users = new ArrayList<DriveUser>();
        users.add(driveUserDao.addDriveUser(drive.getDriveId(), user.getId(), drive.getStart(), drive.getStop(), IS_DRIVER, IS_ACCEPTED));     

        // No reports yet
        List<DriveReport> reports = new ArrayList<DriveReport>();
        
        return new DriveWrap(drive, milestones, users, reports);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Drive putDrive(Drive drive) throws URISyntaxException {
    	return driveDao.updateDrive(drive.getDriveId(),  drive.getStart(), drive.getStop(), drive.getDateTime(), drive.getComment(), drive.getCarBrand(), drive.getCarModel(), drive.getCarYear(), drive.getCarColor(), drive.getCarLicensePlate(), drive.getCarNumberOfSeats(), drive.getOptLuggage(), drive.getOptWinterTires(), drive.getOptPets(), drive.getOptBicycle(), drive.getOptSkis());
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
    
    @Path("{driveId}")
    @DELETE
    @RolesAllowed(Role.Names.USER)
    public void deleteUser(@PathParam("{driveId}") int driveId) {
    	if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver())
    		driveDao.deleteDrive(driveId);
    }
    
    @Path("{driveId}/user")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void addUserToDrive(@PathParam("{driveId}") int driveId, DriveUser driveUser) {
    	driveUserDao.addDriveUser(driveId, user.getId(), driveUser.getStart(), driveUser.getStop(), !IS_DRIVER, !IS_ACCEPTED);
    }
    
    @Path("{driveId/user/{userId}")
    @PUT
    @RolesAllowed(Role.Names.USER)
    public void acceptUserInDrive(@PathParam("{driveId}") int driveId, @PathParam("{userId}") int userId) {
    	DriveUser driveUser = driveUserDao.getDriveUser(driveId, userId);
    	driveUserDao.updateDriveUser(driveId, userId, driveUser.getStart(), driveUser.getStop(), driveUser.isDriver(), IS_ACCEPTED);
    }
    
    @Path("{driveId/user/{userId}")
    @DELETE
    @RolesAllowed(Role.Names.USER)
    public void removeUserFromDrive(@PathParam("{driveId}") int driveId, @PathParam("{userId}") int userId) {
    	driveUserDao.deleteDriveUser(driveId, userId);
    }
    
    
}
