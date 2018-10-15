window.base = window.base || {};

window.base.createDriveController = (() => {
    const model = {
        drive: {},
        driveWrap: {},
        user: {}
    };

    const view = {
        renderDrive: id => window.base.rest.getDrive(id).then(dw => {
            model.driveWrap = dw;
            console.log(dw);
            return dw;
        }),
        renderUser: () => window.base.rest.getUser().then(u => {
            model.user = u;
            console.log(u);
        }).then(u => {
            window.base.rest.getDriveForUser(model.user.userId).then(d => {
                console.log(d);
                if (d === null) {
                    document.getElementById('drive-header').value = 'Create Drive';
                } else {
                    model.drive = d;
                    console.log(d);
                    document.getElementById('drive-header').value = 'Edit Drive';
                }
            })
        })
    };

    const controller = {
        setInput: () => {
            document.getElementById('set-')
        },
        
        removeStop: () => {
            const counter = document.querySelectorAll("#stop-row .stop-div").length;
            console.log(counter);
            if (counter > 0){
                document.getElementById('stop-' + counter).remove();
            }
        },

        addStop: () => {
            const counter = document.querySelectorAll("#stop-row .stop-div").length;
            const stopDiv = document.createElement('div');
            const col = document.createElement('div')
            const colMd = document.createElement('div');
            col.className = 'col';
            colMd.className = 'col-md';
            stopDiv.className = 'row mt-2 stop-div';
            stopDiv.id = 'stop-' + (counter + 1);
            const textArea = document.createElement('textarea');
            const label = document.createElement('label');
            label.innerHTML = 'Stop ' + (counter + 1);
            textArea.id = 'set-stop-' + (counter+1);
            textArea.className = 'form-control';
            colMd.append(label);
            colMd.appendChild(textArea);
            col.append(colMd);
            stopDiv.append(col);
            document.getElementById('stop-row').append(stopDiv);
        },

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

            const milestones = [];
            let driveMilestone;

            for (var i = 1; i <= document.querySelectorAll("#stop-row .stop-div").length; i++) {
                const milestoneId = i;
                const milestone = document.getElementById('set-stop-' + i).value;
                console.log(milestone);
                driveMilestone = {milestoneId, driveId, milestone, departureTime};
                milestones.push(driveMilestone);
                console.log(driveMilestone);
                console.log(milestones);
            }

            const user = model.user;
            const users = [user];

            const reports = [];

            const driveWrap = {drive, milestones, users, reports};

            window.base.rest.addDrive(driveWrap).then(d => {
                if (d.error) {
                    alert(d.error);
                }
            }).then(() => {
                view.renderDrive(1);
                view.renderDrive(2);
                view.renderDrive(3);
                view.renderUser();
            });
        },

        updateDrive: () => window.base.rest.updateDrive(),

        deleteDrive: () => {
            widow.base.rest.deleteDrive(model.drive.driveId);
        },

        load: () => {
            document.getElementById('user-form').onsubmit = controller.createDrive;
            document.getElementById('delete-drive-btn').onclick = controller.deleteDrive;
            document.getElementById('add-stop-btn').onclick = controller.addStop;
            document.getElementById('remove-stop-btn').onclick = controller.removeStop;
            //view.renderDrive();
            view.renderUser();
        }
    };

    return controller;
});