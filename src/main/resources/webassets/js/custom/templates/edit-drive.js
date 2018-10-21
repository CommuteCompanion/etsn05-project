window.base = window.base || {};

window.base.editDriveController = (() => {
    const model = {
        driveWraps: {},
        driveWrap: {},
        user: {},
        theId: {},
        searchQuery: {}
    };

    const view = {
        render: () => window.base.rest.getUser().then(u => {
            model.user = u;
        }).then(() => {
            window.base.rest.getDrivesForUser(model.user.userId).then(d => {
                model.driveWraps = d;
                if (d.length === 0 || model.theId.id === undefined) {
                    document.getElementById('drive-header').innerHTML = 'Create Drive';
                    document.getElementById('delete-drive-btn').remove();
                } else {
                    document.getElementById('drive-header').innerHTML = 'Edit Drive';
                    document.getElementById('create-drive-btn').innerHTML = 'Edit Drive';
                    for (let i = 0; i < d.length; i++) {
                        if (d[i].drive.driveId == model.theId.id) {
                            model.driveWrap = d[i];
                        }
                    }
                }
            }).then(() => {
                if (model.driveWraps.length === 0 || model.theId.id === undefined) {
                }else {
                    controller.setInput();
                }
            });
        })
    };

    const controller = {
        setInput: () => {

            Date.prototype.yyyymmdd = function () {
                const mm = this.getMonth() + 1;
                const dd = this.getDate();

                return [this.getFullYear(),
                        (mm > 9 ? '' : '0') + '-' + mm,
                        (dd > 9 ? '' : '0') + '-' + dd
                       ].join('');
            };

            document.getElementById('set-from').value = model.driveWrap.drive.start;
            document.getElementById('set-to').value = model.driveWrap.drive.stop;

            const leaveDate = new Date(model.driveWrap.drive.departureTime);
            const arrivalDate = new Date(model.driveWrap.drive.arrivalTime);

            document.getElementById('set-date').value = leaveDate.yyyymmdd();
            document.getElementById('set-time-leave').value = leaveDate.toString().split(' ')[4];
            document.getElementById('set-time-arrival').value = arrivalDate.toString().split(' ')[4];
            document.getElementById('set-comment').value = model.driveWrap.drive.comment;
            document.getElementById('set-brand').value = model.driveWrap.drive.carBrand;
            document.getElementById('set-model').value = model.driveWrap.drive.carModel;
            document.getElementById('set-color').value = model.driveWrap.drive.carColor;
            document.getElementById('set-license').value = model.driveWrap.drive.carLicensePlate;
            document.getElementById('set-seats').value = model.driveWrap.drive.carNumberOfSeats;

            document.getElementById('set-tires').checked = model.driveWrap.drive.optWinterTires === true;
            document.getElementById('set-bikes').checked = model.driveWrap.drive.optBicycle === true;
            document.getElementById('set-pets').checked = model.driveWrap.drive.optPets === true;

            if (model.driveWrap.drive.optLuggageSize === 0) {
                document.getElementById('set-luggage').options[0].selected = true;
            } else if (model.driveWrap.drive.optLuggageSize === 1) {
                document.getElementById('set-luggage').options[1].selected = true;
            } else {
                document.getElementById('set-luggage').options[2].selected = true;
            }

            const stopMilestones = model.driveWrap.milestones;
            for(let i = 0; i < stopMilestones.length; i++){
                controller.addStop(stopMilestones[i].milestone, stopMilestones[i].departureTime);
            }

            //Get passengers for drive
            const nbrPassengers = model.driveWrap.users.length;
            for(let i = 1; i < nbrPassengers; i++) {
                if (model.driveWrap.users[i].accepted === true && model.theId.id != undefined) {
                    const userId = model.driveWrap.users[i].userId;
                    const nameCol = document.createElement('div');
                    const nameText = document.createElement('p');

                    nameCol.className = 'col-6 mt-3';
                    nameCol.id = 'remove-col-' + i;
                    window.base.rest.getUser(model.driveWrap.users[i].userId).then(u => {
                        nameText.innerHTML = u.firstName;
                    });

                    const removeCol = document.createElement('div');
                    const removeBtn = document.createElement('button');

                    removeCol.className = 'col-6 mt-3';
                    removeCol.id = 'remove-col-' + i;
                    removeBtn.type = 'button';
                    removeBtn.className = 'btn btn-danger w-100';
                    removeBtn.innerHTML = 'Remove';
                    removeBtn.id = 'removePass-' + i;
                    (function(i){
                        removeBtn.onclick = (function () {
                            document.getElementById('remove-col-' + i).remove();
                            document.getElementById('remove-col-' + i).remove();
                            document.getElementById('remove-col-' + i).remove();
                            window.base.rest.removeUserFromDrive(model.driveWrap.drive.driveId, userId);
                        });
                    })(i);

                    nameCol.append(nameText);
                    removeCol.append(removeBtn);
                    document.getElementById('passenger-row').append(nameCol);
                    document.getElementById('passenger-row').append(removeCol);
                } else if (model.driveWrap.users[i].accepted === false && model.theId.id != undefined){
                    const userId = model.driveWrap.users[i].userId;
                    const nameCol = document.createElement('div');
                    const nameText = document.createElement('p');

                    nameCol.className = 'col-6 mt-3';
                    nameCol.id = 'remove-col-' + i;
                    window.base.rest.getUser(model.driveWrap.users[i].userId).then(u => {
                        nameText.innerHTML = u.firstName;
                    });
                    const acceptCol = document.createElement('div');
                    const acceptBtn = document.createElement('button');

                    acceptCol.className = 'col-3 mt-3';
                    acceptCol.id = 'remove-col-' + i;
                    acceptBtn.type = 'button';
                    acceptBtn.className = 'btn btn-success w-100';
                    acceptBtn.innerHTML = 'Accept';
                    acceptBtn.id = 'removePass-' + i;
                    (function(i){
                        removeBtn.onclick = (function () {
                            model.driveWrap.users[i].accepted = true;
                            acceptBtn.className = 'btn btn-danger w-100';
                            controller.updateDrive(model.driveWrap);
                            view.render(model.theId.id);
                        });
                    })(i);
                    
                    const declineCol = document.createElement('div');
                    const declineBtn = document.createElement('button');
                    
                    declineCol.className = 'col-3 mt-3';
                    declineCol.id = 'remove-col-' + i;
                    declineBtn.type = 'button';
                    declineBtn.className = 'btn btn-danger w-100';
                    declineBtn.innerHTML = 'Decline';
                    declineBtn.id = 'removePass-' + i;
                    (function(i){
                        declineBtn.onclick = (function () {
                            model.driveWrap.users[i].splice(i, 1);
                            declineBtn.className = 'btn btn-danger w-100';
                            controller.updateDrive(model.driveWrap);
                            view.render(model.theId.id);
                        });
                    })(i);

                    nameCol.append(nameText);
                    acceptCol.append(acceptBtn);
                    declineCol.append(declineBtn);
                    document.getElementById('passenger-row').append(nameCol);
                    document.getElementById('passenger-row').append(acceptCol);
                    document.getElementById('passenger-row').append(declineCol);
                }
            }
        },

        removeStop: () => {
            const counter = document.querySelectorAll("#stop-row .stop-div").length;
            if (counter > 0) {
                document.getElementById('stop-' + counter).remove();
            }
        },

        addStop: (text, deparTime) => {
            if (typeof(text) == 'object'){
                const counter = document.querySelectorAll("#stop-row .stop-div").length;
                const stopDiv = document.createElement('div');
                const colName = document.createElement('div');
                const colTime = document.createElement('div');
                const colMdName = document.createElement('div');
                const colMdTime = document.createElement('div');
                const inputTime = document.createElement('input');
                const inputName = document.createElement('input');
                const labelName = document.createElement('label');
                const labelTime = document.createElement('label');

                colName.className = 'col';
                colMdName.className = 'col-md';
                colTime.className = 'col';
                colMdTime.className = 'col-md';
                stopDiv.className = 'row mt-4 stop-div';
                stopDiv.id = 'stop-' + (counter + 1);
                labelName.innerHTML = 'Name of stop ' + (counter + 1);
                labelName.for = 'set-stop-name-' + (counter+1);
                labelTime.innerHTML = 'Departure time for stop ' + (counter + 1);
                labelTime.for = 'set-stop-time-' + (counter+1);
                inputName.type = 'text';
                inputName.placeholder = 'Enter the city where you will stop...';
                inputName.id = 'set-stop-name-' + (counter+1);
                inputName.className = 'form-control';
                inputTime.type = 'text';
                inputTime.id = 'set-stop-time-' + (counter+1);
                inputTime.placeholder = 'HH:MM';
                inputTime.className = 'form-control';
                colMdTime.append(labelTime);
                colMdName.append(labelName);
                colMdTime.appendChild(inputTime)
                colMdName.appendChild(inputName);
                colTime.append(colMdTime);
                colName.append(colMdName);
                stopDiv.append(colName);
                stopDiv.append(colTime);
                document.getElementById('stop-row').append(stopDiv);
                inputName.value = '';
            } else {
                const counter = document.querySelectorAll("#stop-row .stop-div").length;
                const stopDiv = document.createElement('div');
                const colName = document.createElement('div');
                const colTime = document.createElement('div');
                const colMdName = document.createElement('div');
                const colMdTime = document.createElement('div');
                const inputTime = document.createElement('input');
                const inputName = document.createElement('input');
                const labelName = document.createElement('label');
                const labelTime = document.createElement('label');
                const depTime = new Date(deparTime);

                colName.className = 'col';
                colMdName.className = 'col-md';
                colTime.className = 'col';
                colMdTime.className = 'col-md';
                stopDiv.className = 'row mt-4 stop-div';
                stopDiv.id = 'stop-' + (counter + 1);
                labelName.innerHTML = 'Name of stop ' + (counter + 1);
                labelName.for = 'set-stop-name-' + (counter+1);
                labelTime.innerHTML = 'Departure time for stop ' + (counter + 1);
                labelTime.for = 'set-stop-time-' + (counter+1);
                inputName.type = 'text';
                inputName.placeholder = 'Enter the city where you will stop...';
                inputName.id = 'set-stop-name-' + (counter+1);
                inputName.className = 'form-control';
                inputTime.type = 'text';
                inputTime.id = 'set-stop-time-' + (counter+1);
                inputTime.placeholder = 'HH:MM';
                inputTime.className = 'form-control';
                colMdTime.append(labelTime);
                colMdName.append(labelName);
                colMdTime.appendChild(inputTime)
                colMdName.appendChild(inputName);
                colTime.append(colMdTime);
                colName.append(colMdName);
                stopDiv.append(colName);
                stopDiv.append(colTime);
                document.getElementById('stop-row').append(stopDiv);
                inputName.value = text;
                inputTime.value = depTime.toString().split(' ')[4];
            }
        },

        createDrive: submitEvent => {
            submitEvent.preventDefault();
            
            let optWinterTires;
            let optBicycle;
            let optPets;
            const start = document.getElementById('set-from').value;
            const stop = document.getElementById('set-to').value;
            const date = document.getElementById('set-date').value;

            const arrivalValue = document.getElementById('set-time-arrival').value;
            const arrivalString = date + "T" + arrivalValue + ":+02:00";
            const arrivalSplit = arrivalString.split(/[^0-9]/);
            const arrivalDate = new Date (arrivalSplit[0], arrivalSplit[1]-1, arrivalSplit[2], arrivalSplit[3], arrivalSplit[4], arrivalSplit[5]);
            const arrivalTime = arrivalDate.getTime();

            const comment = document.getElementById('set-comment').value;
            const carBrand = document.getElementById('set-brand').value;
            const carModel = document.getElementById('set-model').value;

            const departureValue = document.getElementById('set-time-leave').value;
            const departureString = date + "T" + departureValue + ":+02:00";
            const departureSplit = departureString.split(/[^0-9]/);
            const departureDate = new Date (departureSplit[0], departureSplit[1]-1, departureSplit[2], departureSplit[3], departureSplit[4], departureSplit[5]);
            const departureTime = departureDate.getTime();

            const carColor = document.getElementById('set-color').value;
            const carLicensePlate = document.getElementById('set-license').value;
            const carNumberOfSeats = document.getElementById('set-seats').value;
            const tmpLuggage = document.getElementById('set-luggage');
            const optLuggageSize = tmpLuggage.options[tmpLuggage.selectedIndex].value;

            optWinterTires = document.getElementById('set-tires').checked;
            optBicycle = document.getElementById('set-bikes').checked;
            optPets = document.getElementById('set-pets').checked;

            let driveId;
            if (model.theId.id === undefined){
                driveId = 0;
            } else {
                driveId = model.theId.id;
            }

            const drive = {
                driveId,
                start,
                stop,
                departureTime,
                arrivalTime,
                comment,
                carBrand,
                carModel,
                carColor,
                carLicensePlate,
                carNumberOfSeats,
                optLuggageSize,
                optWinterTires,
                optBicycle,
                optPets
            };

            const milestones = [];
            let driveMilestone;

            for (let i = 1; i <= document.querySelectorAll("#stop-row .stop-div").length; i++) {
                const milestoneId = i;
                const milestone = document.getElementById('set-stop-name-' + i).value;
                const stopValue = document.getElementById('set-stop-time-' + i).value;
                const stopString = date + "T" + stopValue + ":+02:00";
                const stopSplit = stopString.split(/[^0-9]/);
                const stopDate = new Date (stopSplit[0],stopSplit[1]-1,stopSplit[2],stopSplit[3],stopSplit[4],stopSplit[5] );
                const departureTime = stopDate.getTime();
                driveMilestone = {milestoneId, driveId, milestone, departureTime};
                milestones.push(driveMilestone);
            }

            const userId = model.user.userId;
            const driver = true;
            const accepted = true;
            const rated = false;
            const driveUser = {driveId, userId, start, stop, driver, accepted, rated};
            const users = [driveUser];

            const reports = [];

            const driveWrap = {drive, milestones, users, reports};


            if (model.theId.id === undefined){
                let error = false;
                window.base.rest.addDrive(driveWrap).then(d => {
                    if (d.error) {
                        alert("Could not create drive, are you already in a drive at that time?");
                        error = true;
                    } else {
                        model.searchQuery.driveId = d.drive.driveId;
                        model.searchQuery.tripStart = d.drive.start;
                        model.searchQuery.tripStop = d.drive.stop;
                        model.searchQuery.tripStartTime = d.drive.departureTime;
                        model.driveWrap = d;
                        model.theId.id = model.driveWrap.drive.driveId;
                    }
                }).then(() => {
                    if (!error) {
                        controller.loadDrivePage();
                    }
                });
            } else {
                controller.updateDrive(driveWrap);
            }
        },

        loadDrivePage: () => {
            fetch('templates/drive.html')
                .then(response => response.text())
                .then(tabHtml => {
                    document.getElementById('main-tab').innerHTML = tabHtml;
                    window.base.driveController().loadQuery(model.searchQuery);
                });
        },

        updateDrive: drive => {
            let error = false;
            window.base.rest.putDrive(model.theId.id, drive).then((d) => {
                if (d.error) {
                    alert("Could not create drive, are you already in a drive at that time?");
                    error = true;
                } else {
                    model.searchQuery.driveId = d.drive.driveId;
                    model.searchQuery.tripStart = d.drive.start;
                    model.searchQuery.tripStop = d.drive.stop;
                    model.searchQuery.tripStartTime = d.drive.departureTime;
                }
            }).then(() => {
                if (!error) {
                    controller.loadDrivePage();
                }
            });
        },

        deleteDrive: () => window.base.rest.deleteDrive(model.driveWrap.drive.driveId)
        .then(() => window.location.replace('/#/my-commutes')),

        load: (id) => {
            model.theId.id = id;
            document.getElementById('set-date');
            document.getElementById('user-form').onsubmit = controller.createDrive;
            document.getElementById('delete-drive-btn').onclick = controller.deleteDrive;
            document.getElementById('add-stop-btn').onclick = controller.addStop;
            document.getElementById('remove-stop-btn').onclick = controller.removeStop;

            let today = new Date();
            let dd = today.getDate();
            let mm = today.getMonth()+1;
            const yyyy = today.getFullYear();
            if (dd < 10) {
                dd = '0' + dd
            } 
            if (mm < 10){
                mm = '0' + mm
            } 
            today = yyyy + '-' + mm + '-' + dd;
            document.getElementById("set-date").setAttribute("min", today);

            view.render();
        },

        loadWithUserId: (id) => {
            // Change the hash without firing hashchange
            history.pushState({}, ' ', '#/edit-drive');

            controller.load(id);
        },
    };
    
    return controller;
});