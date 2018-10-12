window.base = window.base || {};

window.base.reportedDrivesController = (() => {
    var model = {
        drives: [],
    };

    const controller = {
        load: function() {
            const driveId = 0;
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
            base.rest.addDrive(driveWrap).then(d => {
                alert(d.message);
            });
            
            //base.rest.getReportedDrives().then(function(drives) {
            //    model.drives = drives;
            //    return drives;
            //});
            //alert(model.drives);
        }
    };

    return controller;
});