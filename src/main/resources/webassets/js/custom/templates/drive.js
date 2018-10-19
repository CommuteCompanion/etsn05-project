window.base = window.base || {};

window.base.driveController = (() => {
    let model = {
        user: {},
        driveUser: {},
        driveWrap: {},
        driveReport: {}
    };

    const view = {
        showReportModal: () => {
            new Modal(document.getElementById('report-modal'), {keyboard: false}).show();
        },

        showFailure: (msg, element) => {
            let alertClasses = element.classList;

            alertClasses.remove('d-none');
            alertClasses.remove('alert-info');
            alertClasses.add('alert-danger');

            element.innerHTML = `<h5 class="alert-heading">Oops!</h5><p>Something went wrong, error message: ${msg}.</p>`;
        },

        showSuccess: (msg, element) => {
            let alertClasses = element.classList;

            alertClasses.remove('d-none');
            alertClasses.remove('alert-danger');
            alertClasses.add('alert-info');

            element.innerHTML = `<h5 class="alert-heading">Done</h5><p>${msg}</p>`;
        },

        renderDrive: () => {
            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            const drive = model.driveWrap.drive;
            const driveUsers = model.driveWrap.users;
            let driver;
            let passengerHtml = '';
            let acceptedPassengers = 1;

            for (let i = 0; i < driveUsers.length; i++) {
                if (driveUsers[i].driver) {
                    driver = driveUsers[i].info;
                }
            }

            const today = new Date();
            const dob = new Date(driver.dateOfBirth);
            let driverAge = today.getFullYear() - dob.getFullYear();
            const m = today.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                driverAge--;
            }

            let noDrives;
            let driverReviews;
            let driverRating;

            const requestButton = document.getElementById('drive-request');

            for (let i = 0; i < driveUsers.length; i++) {
                const user = driveUsers[i];
                const firstName = user.info.firstName;
                const score = user.info.ratingTotalScore;
                const reviews = user.info.numberOfRatings;
                const rating = reviews === 0 ? 'No' : parseFloat(score / reviews).toFixed(2);

                if (user.userId === model.user.userId) {
                    requestButton.setAttribute('disabled', '');
                    requestButton.textContent = 'Requested';
                }

                if (user.userId === model.user.userId && user.driver) {
                    requestButton.textContent = 'Driving';
                }

                if (user.driver) {
                    noDrives = user.noDrives;
                    driverReviews = reviews;
                    driverRating = rating;
                } else if (user.accepted) {
                    acceptedPassengers++;
                    passengerHtml += `${firstName} (<i class="fas fa-star fa-sm">${rating}), `;
                }
            }

            passengerHtml = passengerHtml.length === 0 ? 'None' : passengerHtml.slice(0, passengerHtml.length - 2);

            const dtd = new Date(drive.departureTime);
            let departureTime = months[dtd.getMonth()] + ' ' + dtd.getDate() + ' at '+ controller.parseTime(dtd);


            let luggageText = '';
            switch (drive.optLuggageSize) {
                case 1:
                    luggageText = 'Small sized luggage only';
                    break;
                case 2:
                    luggageText = 'Medium sized luggage only';
                    break;
                case 3:
                    luggageText = 'Big luggage ok';
                    break;
            }

            const winterTiresText = drive.optWinterTires ? 'Has winter tires' : 'Does not have winter tires';
            const bicycleText = drive.optBicycle ? 'Bicycle ok' : 'No bicycle';
            const petsText = drive.optPets ? 'Pets ok' : 'No pets';

            // Set content
            document.getElementById('driver-first-name').textContent = driver.firstName;
            document.getElementById('driver-gender').textContent = driver.gender === 0 ? 'Male' : 'Female';
            document.getElementById('driver-age').textContent = driverAge;
            document.getElementById('drive-name').textContent = drive.start + ' to ' + drive.stop;
            document.getElementById('driver-driven').textContent = noDrives;
            document.getElementById('driver-rating').textContent = driverRating;
            document.getElementById('driver-reviews').textContent = driverReviews;
            document.getElementById('driver-driven').textContent = noDrives;
            document.getElementById('driver-rating').textContent = driverRating;
            document.getElementById('driver-reviews').textContent = driverReviews;
            document.getElementById('drive-name').textContent = drive.start + ' to ' + drive.stop;
            document.getElementById('drive-departure-time').textContent = departureTime;
            document.getElementById('drive-pickup').textContent = model.driveUser.start;
            document.getElementById('drive-pickup-time').textContent = model.driveUser.startTime;
            document.getElementById('drive-dropoff').textContent = model.driveUser.stop;
            document.getElementById('drive-comment').textContent = drive.comment.length > 0 ? '"' + drive.comment + '"' : '';
            document.getElementById('car-brand').textContent = drive.carBrand;
            document.getElementById('car-model').textContent = drive.carModel;
            document.getElementById('car-color').textContent = drive.carColor;
            document.getElementById('car-license-plate').textContent = drive.carLicensePlate;
            document.getElementById('car-license-plate-link').setAttribute('href', 'https://biluppgifter.se/fordon/' + drive.carLicensePlate);
            document.getElementById('opt-luggage-icon').classList.add(drive.optLuggageSize > 0 ? 'text-info' : 'text-muted');
            document.getElementById('opt-winter-tires-icon').classList.add(drive.optWinterTires ? 'text-info' : 'text-muted');
            document.getElementById('opt-bicycle-icon').classList.add(drive.optBicycle ? 'text-info' : 'text-muted');
            document.getElementById('opt-pets-icon').classList.add(drive.optPets ? 'text-info' : 'text-muted');
            document.getElementById('opt-luggage-text').textContent = luggageText;
            document.getElementById('opt-winter-tires-text').textContent = winterTiresText;
            document.getElementById('opt-bicycle-text').textContent = bicycleText;
            document.getElementById('opt-pets-text').textContent = petsText;
            document.getElementById('drive-passengers').innerHTML = passengerHtml;
            document.getElementById('drive-seats-left').textContent = drive.carNumberOfSeats - acceptedPassengers;
            document.getElementById('drive-content').classList.remove('d-none');
        }
    };

    const controller = {
        parseTime: date => {
            let hours = date.getHours();
            let minutes = date.getMinutes();
            hours = hours < 10 ? '0' + hours : hours;
            minutes = minutes < 10 ? '0' + minutes : minutes;

            return hours + ':' + minutes;
        },

        getDrive: () => window.base.rest.getDriveWrap(model.driveUser.driveId)
            .then(driveWrap => model.driveWrap = driveWrap),

        submitRequest: () => window.base.rest.requestSeat(model.driveUser.driveId, model.driveUser)
            .then(e => {
                if (e.error) {
                    view.showFailure(e.message, document.getElementById('request-alert-box'));
                } else {
                    view.showSuccess('We\'ve sent a request to the driver letting the person know you want a seat.', document.getElementById('request-alert-box'));
                    const requestButton = document.getElementById('drive-request');
                    requestButton.setAttribute('disabled', '');
                    requestButton.textContent = 'Requested';
                }
            }),

        submitReport: e => {
            e.preventDefault();

            model.driveReport.reportId = 0;
            model.driveReport.driveId = model.driveUser.driveId;
            model.driveReport.reportedByUser = model.user.userId;
            model.driveReport.reportMessage = document.getElementById('report-message').value;

            window.base.rest.reportDrive(model.driveUser.driveId, model.driveReport)
                .then(e => {
                    if (e.error) {
                        view.showFailure(e.message, document.getElementById('report-alert-box'));
                    } else {
                        view.showSuccess('Your report has been sent', document.getElementById('report-alert-box'));
                        document.getElementById('report-message').value = '';
                    }
                });
        },

        getUser: () => window.base.rest.getUser().then(u => {
            model.user = u;
            return model.driveUser.userId = u.userId;
        }),

        getDriverAndPassengers: () => {
            const driveUsers = model.driveWrap.users;

            return Promise.all(driveUsers.map((driveUser, i) => {
                return window.base.rest.getUser(driveUser.userId)
                    .then(user => model.driveWrap.users[i].info = user)
                    .then(() => {
                        if (driveUser.driver) {
                            return window.base.rest.getNumberOfDrivesForUser(driveUser.userId);
                        }

                        return 'N/A';
                    })
                    .then(noDrives => {
                        if (noDrives !== 'N/A') {
                            model.driveWrap.users[i].noDrives = noDrives;
                        }
                    })
            }));
        },

        loadQuery: searchQuery => {
            if (typeof searchQuery === 'undefined' && !Object.keys(model.driveUser).length) {
                window.base.changeLocation('/');
            }

            // Change the hash without firing hashchange
            history.pushState({}, '', '#/drive');

            document.getElementById('drive-request').onclick = controller.submitRequest;
            document.getElementById('report-modal-trigger').onclick = view.showReportModal;
            document.getElementById('report-form').onsubmit = controller.submitReport;

            model.driveUser = {
                driveId: searchQuery.driveId,
                start: searchQuery.tripStart,
                stop: searchQuery.tripStop,
                startTime: searchQuery.tripStartTime,
                driver: false,
                accepted: false,
                rated: false
            };

            controller.getUser()
                .then(() => controller.getDrive())
                .then(() => controller.getDriverAndPassengers())
                .then((() => view.renderDrive()))
        },

        load: () => controller.loadQuery()
    };

    return controller;
});