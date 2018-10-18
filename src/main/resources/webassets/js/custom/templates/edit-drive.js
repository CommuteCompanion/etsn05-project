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
        }).then(u => {
            window.base.rest.getDrivesForUser(model.user.userId).then(d => {
                model.driveWraps = d;
                console.log(d);
                if (d.length === 0 || model.theId.id === undefined) {
                    document.getElementById('drive-header').innerHTML = 'Create Drive';
                    if(document.getElementById('passenger-header') != null){
                        document.getElementById('passenger-header').remove();
                        document.getElementById('passenger-col').remove();
                    }
                } else {
                    document.getElementById('drive-header').innerHTML = 'Edit Drive';
                    document.getElementById('create-drive-btn').innerHTML = 'Edit Drive';
                    for (var i = 0; i < d.length; i++) {
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

            Date.prototype.yyyymmdd = function() {
                var mm = this.getMonth() + 1; // getMonth() is zero-based
                var dd = this.getDate();

                return [this.getFullYear(),
                        (mm>9 ? '' : '0') + '-' + mm,
                        (dd>9 ? '' : '0') + '-' + dd
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
            if (model.driveWrap.drive.optWinterTires === true) {
                document.getElementById('set-tires').checked = true;
            } else {
                document.getElementById('set-tires').checked = false;
            }
            if (model.driveWrap.drive.optBicycle === true) {
                document.getElementById('set-bikes').checked = true;
            } else {
                document.getElementById('set-bikes').checked = false;
            }
            if (model.driveWrap.drive.optPets === true) {
                document.getElementById('set-pets').checked = true;
            } else {
                document.getElementById('set-pets').checked = false;
            }
            if (model.driveWrap.drive.optLuggageSize === 0) {
                document.getElementById('set-luggage').options[0].selected = true;
            } else if (model.driveWrap.drive.optLuggageSize === 1) {
                document.getElementById('set-luggage').options[1].selected = true;
            } else {
                document.getElementById('set-luggage').options[2].selected = true;
            }
            const stopMilestones = model.driveWrap.milestones;
            console.log(stopMilestones);
            for(var i = 0; i < stopMilestones.length; i++){
                controller.addStop(stopMilestones[i].milestone, stopMilestones[i].departureTime);
            }

            //Get passengers for drive
            const nbrPassengers = model.driveWrap.users.length;
            for(var i = 1; i < nbrPassengers; i++) {
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
                        removeBtn.onclick = (function (callback) {
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
                    const removeCol = document.createElement('div');
                    const removeBtn = document.createElement('button');
                    removeCol.className = 'col-6 mt-3';
                    removeCol.id = 'remove-col-' + i;
                    removeBtn.type = 'button';
                    removeBtn.className = 'btn btn-success w-100';
                    removeBtn.innerHTML = 'Accept';
                    removeBtn.id = 'removePass-' + i;
                    (function(i){
                        removeBtn.onclick = (function (callback) {
                            model.driveWrap.users[i].accepted = true;
                            removeBtn.className = 'btn btn-danger w-100';
                            controller.updateDrive(model.driveWrap);
                            view.render(model.theId.id);
                        });
                    })(i);
                    nameCol.append(nameText);
                    removeCol.append(removeBtn);
                    document.getElementById('passenger-row').append(nameCol);
                    document.getElementById('passenger-row').append(removeCol);
                }
            }
        },

        removeStop: () => {
            const counter = document.querySelectorAll("#stop-row .stop-div").length;
            if (counter > 0){
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
                const depTime = new Date(deparTime);
                inputName.value = text;
                inputTime.value = depTime.toString().split(' ')[4];
            }
        },

        createDrive: () => {
            let optWinterTires;
            let optBicycle;
            let optPets;
            const start = document.getElementById('set-from').value;
            const stop = document.getElementById('set-to').value;
            const date = document.getElementById('set-date').value;

            const arrivalValue = document.getElementById('set-time-arrival').value;
            const arrivalString = date + "T" + arrivalValue + ":+02:00";
            const arrivalSplit = arrivalString.split(/[^0-9]/);
            const arrivalDate = new Date (arrivalSplit[0],arrivalSplit[1]-1,arrivalSplit[2],arrivalSplit[3],arrivalSplit[4],arrivalSplit[5] );
            const arrivalTime = arrivalDate.getTime();

            const comment = document.getElementById('set-comment').value;
            const carBrand = document.getElementById('set-brand').value;
            const carModel = document.getElementById('set-model').value;

            const departureValue = document.getElementById('set-time-leave').value;
            const departureString = date + "T" + departureValue + ":+02:00";
            const departureSplit = departureString.split(/[^0-9]/);
            const departureDate = new Date (departureSplit[0],departureSplit[1]-1,departureSplit[2],departureSplit[3],departureSplit[4],departureSplit[5] );
            const departureTime = departureDate.getTime();

            const carColor = document.getElementById('set-color').value;
            const carLicensePlate = document.getElementById('set-license').value;
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

            let driveId;
            if (model.theId.id === undefined){
                driveId = 0;
            } else {
                driveId = model.theId.id;
            }

            const drive = {driveId, start, stop, departureTime, arrivalTime, comment, carBrand, carModel, carColor, carLicensePlate, carNumberOfSeats, optLuggageSize, optWinterTires, optBicycle, optPets};

            const milestones = [];
            let driveMilestone;

            for (var i = 1; i <= document.querySelectorAll("#stop-row .stop-div").length; i++) {
                const milestoneId = i;
                const milestone = document.getElementById('set-stop-name-' + i).value;
                const stopValue = document.getElementById('set-stop-time-' + i).value;
                const stopString = date + "T" + stopValue + ":+02:00";
                const stopSplit = stopString.split(/[^0-9]/);
                const stopDate = new Date (stopSplit[0],stopSplit[1]-1,stopSplit[2],stopSplit[3],stopSplit[4],stopSplit[5] );
                const departureTime = stopDate.getTime();
                console.log(departureTime);
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
                window.base.rest.addDrive(driveWrap).then(d => {
                    if (d.error) {
                        alert(d.error);
                    } else {
                        model.searchQuery.driveId = d.drive.driveId;
                        model.searchQuery.tripStart = d.drive.start;
                        model.searchQuery.tripStop = d.drive.stop;
                        model.searchQuery.tripStartTime = d.drive.departureTime;
                        model.driveWrap = d;
                        model.theId.id = model.driveWrap.drive.driveId;
                    }
                }).then(() => {
                    controller.loadDrivePage();
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

        updateDrive: (drive) => {
            window.base.rest.putDrive(model.theId.id, drive).then((d) => {
                model.searchQuery.driveId = d.drive.driveId;
                model.searchQuery.tripStart = d.drive.start;
                model.searchQuery.tripStop = d.drive.stop;
                model.searchQuery.tripStartTime = d.drive.departureTime;
            }).then(() => {
                controller.loadDrivePage(); 
            });
        },

        deleteDrive: () => window.base.rest.deleteDrive(model.driveWrap.drive.driveId)
        .then(() => window.location.replace('/')),

        load: (id) => {
            model.theId.id = id;
            document.getElementById('set-date')
            document.getElementById('user-form').onsubmit = controller.createDrive;
            document.getElementById('delete-drive-btn').onclick = controller.deleteDrive;
            document.getElementById('add-stop-btn').onclick = controller.addStop;
            document.getElementById('remove-stop-btn').onclick = controller.removeStop;

            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1;
            var yyyy = today.getFullYear();
            if(dd<10){
                dd='0'+dd
            } 
            if(mm<10){
                mm='0'+mm
            } 
            today = yyyy+'-'+mm+'-'+dd;
            document.getElementById("set-date").setAttribute("min", today);

            view.render();
        },

        loadWithUserId: (id) => {
            controller.load(id);
        },
    };
    return controller;
});