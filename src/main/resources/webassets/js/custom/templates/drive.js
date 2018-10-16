window.base = window.base || {};

window.base.driveController = (() => {
    let model = {
        user: {},
        driveUser: {},
        driveWrap: {}
    };

    const view = {
        showFailure: msg => {
            let alert = document.getElementById('alert-box');
            let alertClasses = alert.classList;

            alertClasses.remove('d-none');
            alertClasses.remove('alert-secondary');
            alertClasses.add('alert-danger');

            alert.innerHTML = `<h4 class="alert-heading">Oops!</h4><p>Something went wrong, error message: ${msg}.</p>`;
        },
        showSuccess: () => {
            let alert = document.getElementById('alert-box');
            let alertClasses = alert.classList;

            alertClasses.remove('d-none');
            alertClasses.remove('alert-danger');
            alertClasses.add('alert-secondary');

            alert.innerHTML = `<h4 class="alert-heading">Done</h4><p>We've sent a request to the driver letting the person know you want a seat.</p>`;
        },
        renderDrive: () => {
            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            const drive = model.driveWrap.drive;
            const driveUsers = model.driveWrap.users;
            let driver;
            let passengerHtml = '';

            for (let i = 0; i < driveUsers.length; i++) {
                if (driveUsers[i].driver) {
                    driver = driveUsers[i].info;
                }
            }

            document.getElementById('driver-first-name').textContent = driver.firstName;

            const driverGender = driver.gender === 0 ? 'Male' : 'Female';
            document.getElementById('driver-gender').textContent = driverGender;

            const today = new Date();
            const dob = new Date(driver.dateOfBirth);
            let driverAge = today.getFullYear() - dob.getFullYear();
            const m = today.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                driverAge--;
            }

            document.getElementById('driver-age').textContent = driverAge;

            let noDrives;
            let driverReviews;
            let driverRating;

            for (let i = 0; i < driveUsers.length; i++) {
                const user = driveUsers[i];
                const firstName = user.info.firstName;
                const score = user.info.ratingTotalScore;
                const reviews = user.info.numberOfRatings;
                const rating = reviews === 0 ? 'No' : parseFloat(score / reviews).toFixed(2);

                if (user.driver) {
                    noDrives = user.noDrives;
                    driverReviews = reviews;
                    driverRating = rating;
                } else {
                    passengerHtml += `${firstName} (<i class="fas fa-star fa-sm">${rating})`

                    if (i < driveUsers.length - 1) {
                        passengerHtml += ', ';
                    }
                }
            }

            passengerHtml = passengerHtml.length === 0 ? 'None' : passengerHtml;

            document.getElementById('driver-driven').textContent = noDrives;
            document.getElementById('driver-rating').textContent = driverRating;
            document.getElementById('driver-reviews').textContent = driverReviews;

            const driveName = drive.start + ' to ' + drive.stop;
            document.getElementById('drive-name').textContent = driveName;

            const dtd = new Date(drive.departureTime);

            let departureTime = months[dtd.getMonth()] + ' ';
            departureTime += dtd.getDate() + ' at ';
            departureTime += dtd.getHours() + ':' + dtd.getMinutes();

            document.getElementById('drive-departure-time').textContent = departureTime;

            document.getElementById('drive-pickup').textContent = model.driveUser.start;
            document.getElementById('drive-pickup-time').textContent = model.driveUser.startTime;
            document.getElementById('drive-dropoff').textContent = model.driveUser.stop;

            document.getElementById('drive-comment').textContent = '"' + drive.comment + '"';

            document.getElementById('car-brand').textContent = drive.carBrand;
            document.getElementById('car-color').textContent = drive.carColor;
            document.getElementById('car-license-plate').textContent = drive.carLicensePlate;
            document.getElementById('car-license-plate-link').setAttribute('href', 'https://biluppgifter.se/fordon/' + drive.carLicensePlate);

            document.getElementById('opt-luggage-icon').classList.add(drive.optLuggageSize > 0 ? 'text-info' : 'text-muted');
            document.getElementById('opt-winter-tires-icon').classList.add(drive.optWinterTires ? 'text-info' : 'text-muted');
            document.getElementById('opt-bicycle-icon').classList.add(drive.optBicycle ? 'text-info' : 'text-muted');
            document.getElementById('opt-pets-icon').classList.add(drive.optPets ? 'text-info' : 'text-muted');

            let luggageText = '';
            switch (drive.optLuggageSize) {
                case 0: luggageText = 'No luggage allowed'; break;
                case 1: luggageText = 'Small sized luggage only'; break;
                case 2: luggageText = 'Medium sized luggage only'; break;
                case 3: luggageText = 'Big luggage ok'; break;
            }

            const winterTiresText = drive.optWinterTires ? 'Has winter tires' : 'Does not have winter tires';
            const bicycleText = drive.optWinterTires ? 'Bicycle ok' : 'No bicycle';
            const petsText = drive.optWinterTires ? 'Pets ok' : 'No pets';

            document.getElementById('opt-luggage-text').textContent = luggageText;
            document.getElementById('opt-winter-tires-text').textContent = winterTiresText;
            document.getElementById('opt-bicycle-text').textContent = bicycleText;
            document.getElementById('opt-pets-text').textContent = petsText;

            document.getElementById('drive-passengers').innerHTML = passengerHtml;

            const noSeatsLeft = drive.carNumberOfSeats - driveUsers.length;
            document.getElementById('drive-seats-left').textContent = noSeatsLeft;
        }
    };

    const controller = {
        getDrive: () => window.base.rest.getDriveWrap(model.driveUser.driveId)
            .then(driveWrap => model.driveWrap = driveWrap),
        submitRequest: () => window.base.rest.requestSeat(model.driveUser)
            .catch(e => view.showFailure(e.message))
            .then(() => view.showSuccess()),
        reportDrive: () => {},
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
                window.base.changeLocation('#/search');
            }

            document.getElementById('drive-request').onclick = controller.submitRequest;
            document.getElementById('report-drive').onclick = controller.reportDrive;

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
        load: () => controller.loadQuery(),
        initOnLoad: () => document.addEventListener('DOMContentLoaded', window.base.driveController.load())
    };

    return controller;
})
;