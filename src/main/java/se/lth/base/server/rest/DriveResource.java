package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.mail.MailHandler;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Path("drive")
public class DriveResource {
    private final boolean IS_DRIVER = true;
    private final boolean IS_ACCEPTED = true;
    private final boolean IS_RATED = true;

    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
    private final DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
    private final DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
    private final DriveReportDataAccess driveReportDao = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
    private final UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());
    private final User user;
    private final MailHandler mailHandler = new MailHandler();

    public DriveResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public DriveWrap createDrive(DriveWrap driveWrap) {
        Drive drive = driveWrap.getDrive();
        if(drive.getDepartureTime() < System.currentTimeMillis()) {
        	throw new WebApplicationException("Can't create a drive with a departure time before the current time", Status.PRECONDITION_FAILED);
        }
        List<DriveMilestone> milestones = driveWrap.getMilestones();
        int driveId = drive.getDriveId();
        String start = drive.getStart();
        String stop = drive.getStop();
        
        //Check so that the driver is not creating a drive that overlaps another drive that the driver is associated with
        if (!checkBookingOverlap(user.getId(), drive).isEmpty()) {
            throw new WebApplicationException("This trip is overlapping with another trip that you are on", Status.CONFLICT);
        }
        
        // Add drive
        drive = driveDao.addDrive(drive);
        
        // Add all milestones
        for (DriveMilestone m : milestones) {
            if (milestones.size() < 4) {
                driveMilestoneDao.addMilestone(drive.getDriveId(), m.getMilestone(), m.getDepartureTime());
            } else {
                throw new WebApplicationException("You are only allowed to add 4 milestones to your drive", Status.BAD_REQUEST);
            }
        }
        
        // Add driver to list of users
        List<DriveUser> users = new ArrayList<>();
        users.add(driveUserDao.addDriveUser(drive.getDriveId(), user.getId(), drive.getStart(), drive.getStop(), IS_DRIVER, IS_ACCEPTED, !IS_RATED));
        
        // No reports yet
        List<DriveReport> reports = new ArrayList<>();
        return new DriveWrap(drive, milestones, users, reports);
    }

    @Path("{driveId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public DriveWrap putDrive(@PathParam("driveId") int driveId, DriveWrap driveWrap) {
        if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {
            if (!checkBookingOverlap(user.getId(), driveWrap.getDrive()).isEmpty()) {
                throw new WebApplicationException("This trip is overlapping with another trip that you are on", Status.CONFLICT);
            }
            Drive drive = driveDao.updateDrive(driveWrap.getDrive());
            List<DriveMilestone> currentMilestones = driveMilestoneDao.getMilestonesForDrive(driveId);
            List<DriveMilestone> newMilestones = driveWrap.getMilestones();
            List<DriveMilestone> updatedMilestones = new ArrayList<DriveMilestone>();
            for(DriveMilestone updatedM : newMilestones) {
            	Boolean matched = false;
            	for(DriveMilestone currentM : currentMilestones) {
            		if(currentM.getMilestone() == updatedM.getMilestone()) {
            			driveMilestoneDao.updateMilestone(currentM.getMilestoneId(), updatedM.getMilestone(), updatedM.getDepartureTime());
            			matched = true;
            			updatedMilestones.add(currentM);
            		}
            	}
            	if(!matched) {
            		driveMilestoneDao.addMilestone(updatedM.getDriveId(), updatedM.getMilestone(), updatedM.getDepartureTime());
            	}
            }
            
			// remove the milestones that have been updated from the list and delete the
			// other ones from the database
			currentMilestones.removeAll(updatedMilestones);
			for (DriveMilestone m : currentMilestones) {
				driveMilestoneDao.deleteMilestone(m.getMilestoneId());
			}
			
			// Update users
			for (DriveUser u : driveUserDao.getDriveUsersForDrive(driveId)) {
				driveUserDao.deleteDriveUser(driveId, u.getUserId());
			}
			for (DriveUser u : driveWrap.getUsers()) {
				driveUserDao.addDriveUser(driveId, u.getUserId(), u.getStart(), u.getStop(), u.isDriver(),
						u.isAccepted(), u.hasRated());
			}
			
			// Update reports
			for (DriveReport r : driveReportDao.getDriveReportsForDrive(driveId)) {
				driveReportDao.deleteDriveReport(r.getReportId());
			}
			for (DriveReport r : driveWrap.getReports()) {
				driveReportDao.addDriveReport(driveId, r.getReportedByUserId(), r.getReportMessage());
			}
            return new DriveWrap(drive, driveWrap.getMilestones(), driveWrap.getUsers(), driveWrap.getReports());
        }

        throw new WebApplicationException("Only driver allowed to update drive", Status.UNAUTHORIZED);
    }

    @Path("{driveId}")
    @GET
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveWrap getDrive(@PathParam("driveId") int driveId) {
        Drive drive = driveDao.getDrive(driveId);
        List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
        List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
        List<DriveReport> reports = user.getRole().clearanceFor(Role.ADMIN) ? driveReportDao.getDriveReportsForDrive(driveId) : new ArrayList<>();

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
        if (!driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {
            throw new WebApplicationException("Only driver allowed to delete drive", Status.UNAUTHORIZED);
        }
        Drive drive = driveDao.getDrive(driveId);
        List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
        List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
        List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(driveId);

        DriveWrap driveWrap = new DriveWrap(drive, milestones, users, reports);

        //If there are more users in the drive except for the driver they should recieve an email.
        if (driveWrap.getUsers().size() > 1) {
            try {
                mailHandler.notifyPassengersDriverCancelledDrive(driveWrap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        driveDao.deleteDrive(driveId);
    }

    @Path("{driveId}/user")
    @POST
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveUser addUserToDrive(@PathParam("driveId") int driveId, DriveUser driveUser) {
        List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
        for (DriveUser du : users) {
            if (du.getUserId() == driveUser.getUserId()) {
                throw new WebApplicationException("You are already on this drive", Status.PRECONDITION_FAILED);
            }
        }
        Drive drive = driveDao.getDrive(driveId);
        if (drive.getCarNumberOfSeats() > driveUserDao.getNumberOfUsersInDrive(driveId)) {
            List<Drive> collidingDrives = checkBookingOverlap(driveUser.getUserId(), drive);
            if (collidingDrives.isEmpty()) {
                List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
                List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(driveId);

                DriveWrap driveWrap = new DriveWrap(drive, milestones, users, reports);
                try {
                    mailHandler.notifyDriverNewPassengerRequested(driveWrap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return driveUserDao.addDriveUser(driveId, user.getId(), driveUser.getStart(), driveUser.getStop(), !IS_DRIVER, !IS_ACCEPTED, !IS_RATED);
            } else {
                throw new WebApplicationException("This drive overlaps in time with other booked drives", Status.PRECONDITION_FAILED);
            }
        } else {
            throw new WebApplicationException("No available seats left", Status.PRECONDITION_FAILED);
        }
    }

    @Path("{driveId}/user/{userId}")
    @PUT
    @RolesAllowed(Role.Names.USER)
    public DriveUser acceptUserInDrive(@PathParam("driveId") int driveId, @PathParam("userId") int userId) {
        if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {

            driveUserDao.acceptDriveUser(driveId, userId);

            Drive drive = driveDao.getDrive(driveId);
            List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
            List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
            List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(driveId);

            DriveWrap driveWrap = new DriveWrap(drive, milestones, users, reports);

            try {
                mailHandler.notifyDriverNewPassengerAccepted(driveWrap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mailHandler.notifyPassengerBookingConfirmed(driveWrap, userDao.getUser(userId));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return driveUserDao.getDriveUser(driveId, userId);
        }

        throw new WebApplicationException("Only driver allowed to accept passengers", Status.UNAUTHORIZED);
    }

    @Path("{driveId}/user/{userId}")
    @DELETE
    @RolesAllowed(Role.Names.USER)
    public void removeUserFromDrive(@PathParam("driveId") int driveId, @PathParam("userId") int userId) {
        if (!driveUserDao.getDriveUser(driveId, user.getId()).isDriver() && user.getId() != userId) {
            throw new WebApplicationException("Only driver or yourself allowed to delete", Status.UNAUTHORIZED);
        }

        Drive drive = driveDao.getDrive(driveId);
        List<DriveUser> users = driveUserDao.getDriveUsersForDrive(driveId);
        List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(driveId);
        List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(driveId);

        DriveWrap driveWrap = new DriveWrap(drive, milestones, users, reports);

        try {
            if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {
                mailHandler.notifyPassengerDriverRemovedPassenger(driveWrap, userDao.getUser(userId));
            } else if (user.getId() == userId) {
                mailHandler.notifyDriverPassengerCancelledTrip(driveWrap, userDao.getUser(userId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        driveUserDao.deleteDriveUser(driveId, userId);
    }

    @Path("{driveId}/rate")
    @PUT
    @RolesAllowed(Role.Names.USER)
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public DriveRatingWrap rateUsers(@PathParam("driveId") int driveId, DriveRatingWrap rating) {
        if (driveUserDao.getDriveUser(driveId, user.getId()).hasRated()) {
            throw new WebApplicationException("You have already rated", Status.UNAUTHORIZED);
        }

        if (driveUserDao.getDriveUser(driveId, user.getId()).isDriver()) {
            for (DriveRating dr : rating.getRatings()) {
                userDao.updateUserRating(dr);
            }
        } else {
            userDao.updateUserRating(rating.getRatings().get(0));
        }

        driveUserDao.hasRated(user.getId(), driveId);

        return rating;
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
    public List<DriveWrap> getReportedDrives() {
        List<DriveWrap> driveWraps = new ArrayList<>();
        List<Drive> reportedDrives = driveDao.getReportedDrives();
        attachDriveDetails(reportedDrives, driveWraps);

        return driveWraps;
    }

    @Path("user/{userId}")
    @GET
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<DriveWrap> getDrivesForUser(@PathParam("userId") int userId) {
        if (userId == user.getId() || user.getRole().clearanceFor(Role.ADMIN)) {
            List<DriveWrap> driveWraps = new ArrayList<>();
            List<Drive> drives = driveDao.getDrivesForUser(userId);
            attachDriveDetails(drives, driveWraps);

            return driveWraps;
        }
        throw new WebApplicationException("You do not have access to these drives", Status.UNAUTHORIZED);
    }


    @Path("count/{userId}")
    @GET
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.TEXT_PLAIN)
    public int getNumberOfDrives(@PathParam("userId") int userId) {
        return driveUserDao.getNumberOfDrivesForUser(userId);
    }

    private void attachDriveDetails(List<Drive> drives, List<DriveWrap> driveWraps) {
        for (Drive d : drives) {
            List<DriveMilestone> milestones = driveMilestoneDao.getMilestonesForDrive(d.getDriveId());
            List<DriveUser> users = driveUserDao.getDriveUsersForDrive(d.getDriveId());
            List<DriveReport> reports = driveReportDao.getDriveReportsForDrive(d.getDriveId());
            driveWraps.add(new DriveWrap(d, milestones, users, reports));
        }
    }

    /**
     * Checks if it is possible for a user to book a specific Drive.
     *
     * @param userId the id of user
     * @param requestedDrive the id of the drive that one wishes to see if it is possible to book
     * @return a list of drives associated with this user, conflicting with the entered drive
     */
    private List<Drive> checkBookingOverlap(int userId, Drive requestedDrive) {

        List<Drive> drivesForUser = driveDao.getDrivesForUser(userId);
        List<Drive> conflictingDrives = new ArrayList<>();
        drivesForUser.forEach(d -> {
            if (d.getDriveId() != requestedDrive.getDriveId()) {
                if (requestedDrive.getDepartureTime() > d.getDepartureTime()) {
                    //if departure is larger => departure must also be larger than other drive's arrival
                    if (requestedDrive.getDepartureTime() < d.getArrivalTime()) {
                        conflictingDrives.add(d);
                    }
                } else {
                    //if departure is smaller => arrival must also be smaller than other's departure
                    if (requestedDrive.getArrivalTime() > d.getDepartureTime()) {
                        conflictingDrives.add(d);
                    }
                }
            }
        });

        return conflictingDrives;
    }

    private List<Drive> getDriveAssociatedWithUser(int userId) {
        List<Drive> drives = driveDao.getDrives();
        Iterator<Drive> iterator = drives.iterator();
        while (iterator.hasNext()) {
            Drive drive = iterator.next();
            List<DriveUser> driveUsers = driveUserDao.getDriveUsersForDrive(drive.getDriveId());
            for (int i = 0; i < driveUsers.size(); i++) {
                DriveUser driveUser = driveUsers.get(i);
                if (driveUser.getUserId() == userId) {
                    // User is associated with this drive, keep drive in list
                    break;
                }
                if (i + 1 == driveUsers.size()) {
                    // User is not associated with this drive, remove drive from list
                    iterator.remove();
                }
            }

        }
        return drives;
    }
}
