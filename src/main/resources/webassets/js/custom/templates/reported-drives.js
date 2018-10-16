window.base = window.base || {};

window.base.reportedDrivesController = (() => {
    var model = {
        drives: [],
    };
    var view = {
        getDriverUserId: function(drive) {
            drive.users.forEach(user => {
                if (user.driver === true) {
                    return user.userId;
                }
            });
        },
        getRenderedRating: function(user) {
            if (user.numberOfRatings !== 0) {
                return 'Rating' + user.ratingTotalScore/user.numberOfRatings;
            }
            return 'No rating';
        },
        renderCard: function(drive) {
            //console.log(drive);
            drive.reports.forEach(report => {
                var t = document.getElementById('reported-drives-template');
                var reporter;
                const p1 = window.base.rest.getUser(report.reportedByUserId).then(function(user) {
                    reporter = user;
                });
                var driver;
                var driverId = view.getDriverUserId(drive);
                const p2 = window.base.rest.getUser(driverId).then(function(user) {
                    driver = user;
                });
                Promise.all([p1, driver]).then(function() {
                    //console.log(driver);
                    //console.log(reporter);
                    t.content.querySelector('.report-start-destination-time').textContent 
                        = drive.drive.start + ' to ' + drive.drive.stop 
                        + ', ' + Date(drive.drive.departureTime);

                    t.content.querySelector('.report-driver').textContent 
                        = 'Driver: ' + driver.firstName + ' ' + driver.lastName;

                    t.content.querySelector('.report-driver-rating-warning').textContent 
                        = view.getRenderedRating(driver) + ' | ' + driver.warning + ' warnings';


                    t.content.querySelector('.report-reported-by').textContent
                        = 'Reported by: ' + reporter.firstName + ' ' + reporter.lastName;
                    t.content.querySelector('.report-reported-by-rating-warning').textContent 
                        = view.getRenderedRating(reporter) + ' | ' + reporter.warning + ' warnings';


                    t.content.querySelector('.report-comment').textContent = report.reportMessage;

                    // Associate buttons and cards with correct Id
                    t.content.querySelector('.dismiss-report-button').id   = 'dismiss-report-button' + report.reportId;
                    
                    t.content.querySelector('.reported-drives').id = 'reported-drives' + report.reportId;

                    var clone = document.importNode(t.content, true);
                    t.parentElement.appendChild(clone);
                });

            });
        },
        render: function() {
            model.drives.forEach(drive => view.renderCard(drive));
        },
    };

    const controller = {
        dismissReport: function(user) {
            alert('NOT IMPLEMENTED');
        },
        giveWarning: function(user) {
            window.base.rest.warnUser(user.userId).then(function () {
                user.warning = user.warning + 1;
                document.getElementById('admin-warning'+user.userId).textContent 
                    = user.warning + ' warnings';
                });
        },
        deleteAccount: function(user) {
            window.base.rest.deleteUser(user.userId).then(function () {
                var item = document.getElementById('manage-user-card' + user.userId);
                item.parentElement.removeChild(item);
            }); 
            
        },
        deleteDrive: function(driveWrap) {
            window.base.rest.deleteUser(user.userId).then(function () {
                var item = document.getElementById('manage-user-card' + user.userId);
                item.parentElement.removeChild(item);
            }); 
            
        },
        load: function() {
            window.base.rest.getReportedDrives().then(function(drives) {
                model.drives = drives;
                return drives;
            }).then(function() {
                view.render();
            });
            
            //controller.debug();
        },
        debug: function() {
            // TODO: Add responsiveness to buttons!

            const driveId = 1;
            const start = 'start';
            const stop = 'stop';
            const departureTime = Date.parse('2011-10-10T14:48:00');
            const comment = 'woop';
            const carBrand = 'wolksv';
            const carModel = 'Good';
            const carColor = 'Red';
            const carLicensePlate = 'abc123';
            const carNumberOfSeats = 4;
            const optLuggageSize = 2;
            const optWinterTires = false;
            const optBicycle = false;
            const optPets = false;
            const drive = { driveId, start, stop, departureTime, comment, carBrand, carModel,
                            carColor, carLicensePlate, carNumberOfSeats, optLuggageSize, 
                            optWinterTires, optBicycle, optPets };

            const milestones = [];

            //const driveId = 0; // Declared earlier but still needed for driveuser
            const userId = 1;
            //const start = 'start'; // Same
            //const stop = 'stop';  // Same
            const driver = false;
            const accepted = false;
            const rated = false;

            const driveUser = {driveId, userId, start, stop, driver, accepted, rated};
            const users = [driveUser];
            const reports = [];

            const driveWrap = {
                drive, milestones, users, reports
            };
            //base.rest.addDrive(driveWrap).then(d => {
            //    console.log(d);
            //});

            const reportId = 1;
            //const driveId = 0;
            const reportedByUserId = 1;
            const reportMessage = 'HE HIT ME!';
            const driveReport = {reportId, driveId, reportedByUserId, reportMessage};
            
            base.rest.addReport(driveId, driveReport).then(d => {
                console.log(d);
            });
           // base.rest.getReportedDrives().then( {

            //});

             base.rest.addDrive(driveWrap).then(d => {
                alert(d.message);
            })

           // );
            
            //base.rest.getReportedDrives().then(function(drives) {
            //    model.drives = drives;
            //    return drives;
            //});
            //alert(model.drives);
        }
    };

    return controller;
});