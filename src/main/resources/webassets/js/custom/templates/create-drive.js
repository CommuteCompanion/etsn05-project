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
            let optWinterTires;
            let optBicycle;
            let optPets;
            const start = document.getElementById('set-from').value;
            const stop = document.getElementById('set-to').value;
            const date = document.getElementById('set-date').value;
            const arrivalValue = document.getElementById('set-time-arrival').value;
            const arrivalDate = new Date(date + "T" + arrivalValue + ":00+0000");
            const arrivalTime = arrivalDate.getTime();
            const comment = document.getElementById('set-comment').value;
            const carBrand = document.getElementById('set-brand').value;
            const carModel = document.getElementById('set-model').value;
            const year = document.getElementById('set-year').value;
            const departureValue = document.getElementById('set-time-leave').value;
            const departureDate = new Date(date + "T" + departureValue + ":00+0000");
            const departureTime = departureDate.getTime();
            const carColor = document.getElementById('set-color').value;
            const carLicensePlate = document.getElementById('set-licence').value;
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
            console.log(arrivalTime);
            console.log(comment);
            console.log(carBrand);
            console.log(carModel);
            console.log(year);
            console.log(carColor);
            console.log(carLicensePlate);
            console.log(carNumberOfSeats);
            console.log(optLuggageSize);
            console.log(optWinterTires);
            console.log(optBicycle);
            console.log(optPets);

            const driveId = 0;

            const drive = {driveId, start, stop, departureTime, arrivalTime, comment, carBrand, carModel, carColor, carLicensePlate, carNumberOfSeats, optLuggageSize, optWinterTires, optBicycle, optPets};

            const milestoneId = 0;
            const milestone = comment;

            const driveMilestone = {milestoneId, driveId, milestone, departureTime};

            const milestones = [driveMilestone];

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