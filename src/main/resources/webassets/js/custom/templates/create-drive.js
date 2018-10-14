window.base = window.base || {};

window.base.createDriveController = (() => {
    const model = {
        drive: {},
        user: {}
    };

    const view = {
        renderDrive: () => window.base.rest.getDrive().then(d => {
            model.drive = d;
            console.log(d);
            return d;
        }),
        renderUser: () => window.base.rest.getUser().then(u => {
            model.user = u;
            console.log(u);
            return u;
        })
    };

    const controller = {
        createDrive: () => {
            console.log('hej');
            let optWinterTires;
            let optBicycle;
            let optPets;
            const start = document.getElementById('set-from').value;
            const stop = document.getElementById('set-to').value;
            const date = document.getElementById('set-date').value;
            const departureTime = document.getElementById('set-time').value;
            const comment = document.getElementById('set-comment').value;
            const carBrand = document.getElementById('set-brand').value;
            const carModel = document.getElementById('set-model').value;
            const year = document.getElementById('set-year').value;
            const carColor = document.getElementById('set-color').value;
            const carLicencePlate = document.getElementById('set-licence').value;
            const carNumberOfSeats = document.getElementById('set-seats').value;
            const tmpLuggage = document.getElementById('set-luggage');
            const optLuggageSize = tmpLuggage.options[tmpLuggage.selectedIndex].value;

            if (document.getElementById('set-tires').checked) {
                optWinterTires = true;
            } else {
                optWinterTires = false;
            };
            if (document.getElementById('set-bikes').checked) {
                optBicycle = true;
            } else {
                optBicycle = false;
            };
            if (document.getElementById('set-pets').checked) {
                optPets = true;
            } else {
                optPets = false;
            };

            console.log(start);
            console.log(stop);
            console.log(date);
            console.log(departureTime);
            console.log(comment);
            console.log(carBrand);
            console.log(carModel);
            console.log(year);
            console.log(carColor);
            console.log(carLicencePlate);
            console.log(carNumberOfSeats);
            console.log(optLuggageSize);
            console.log(optWinterTires);
            console.log(optBicycle);
            console.log(optPets);
            
            const driveId = 0;

            const drive = {driveId, start, stop, departureTime, comment, carBrand, carModel, carColor, carLicencePlate, carNumberOfSeats, optLuggageSize, optWinterTires, optBicycle, optPets};

            const milestoneId = 0;
            const milestone = comment;

            const driveMilestone = {milestoneId, driveId, milestone, departureTime};

            const milestones = [milestone];

            const user = model.user;
            const users = [user];

            const reports = [];

            const driveWrap = {drive, milestones, users, reports};

            window.base.rest.addDrive(driveWrap).then(drive => {
                if (drive.error) {
                    alert(drive.error);
                }
            });
        },

        updateDrive: () => window.base.rest.updateDrive(),

        deleteDrive: () => widow.base.rest.deleteDrive(),

        load: () => {
            document.getElementById('user-form').onsubmit = controller.createDrive;
            document.getElementById('delete-drive-btn').onclick = controller.deleteDrive;
            view.renderDrive();
            view.renderUser();
        }
    };

    return controller;
});