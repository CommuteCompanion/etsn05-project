package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.Drive;
import se.lth.base.server.data.DriveDataAccess;
import se.lth.base.server.data.DriveMilestoneDataAccess;
import se.lth.base.server.data.DriveReportDataAccess;
import se.lth.base.server.data.DriveUserDataAccess;
import se.lth.base.server.data.Role;
import se.lth.base.server.data.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;

@Path("drive")
public class DriveResource {

    private final DriveDataAccess driveDao = new DriveDataAccess(Config.instance().getDatabaseDriver());
    private final DriveUserDataAccess driveUserDao = new DriveUserDataAccess(Config.instance().getDatabaseDriver());
    private final DriveMilestoneDataAccess driveMilestoneDao = new DriveMilestoneDataAccess(Config.instance().getDatabaseDriver());
    private final DriveReportDataAccess driveReport = new DriveReportDataAccess(Config.instance().getDatabaseDriver());
    private final User user;

    public DriveResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Drive addDrive(Drive drive) throws URISyntaxException {
        return driveDao.addDrive(user.getId(), drive.getStart(), drive.getStop(), drive.getDateTime(), drive.getComment(), drive.getCarBrand(), drive.getCarModel(), drive.getCarYear(), drive.getCarColor(), drive.getCarLicensePlate(), drive.getCarNumberOfSeats(), drive.getOptLuggage(), drive.getOptWinterTires(), drive.getOptPets(), drive.getOptBicycle(), drive.getOptSkis());
    }
}
