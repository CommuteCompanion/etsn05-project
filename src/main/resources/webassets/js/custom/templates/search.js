window.base = window.base || {};

window.base.searchController = (() => {
    let model = {
        user: {},
        searchResults: [],
        searchQuery: {}
    };

    const view = {
        showFailure: msg => {
            console.log(msg); // to be implemented in to an alert box
        },
        renderSearchResults: () => {
            let searchResultsWrapper = document.getElementById('search-results');
            let searchResults = '';

            for (let i = 0; i < model.searchResults.length; i++) {
                const drive = model.searchResults[i].drive;
                const driveName = drive.start + ' ' + drive.stop;
                const departureTime = drive.departureTime;
                const carBrand = drive.carBrand;
                const carModel = drive.carModel;
                const carYear = drive.carYear;
                const carLicensePlate = drive.carLicensePlate;
                const carNumberOfSeats = drive.carNumberOfSeats;

                const optActive = 'text-info';
                const optNotActive = 'text-muted';
                const optLuggage = drive.optLuggageSize > 0 ? optActive : optNotActive;
                const optWinterTires = drive.optWinterTires ? optActive : optNotActive;
                const optBicycle = drive.optBicycle ? optActive : optNotActive;
                const optPets = drive.optPets ? optActive : optNotActive;

                const users = model.searchResults[i].users;
                let driverFirstName = '';
                let driverGender = '';
                let driverAge = '';
                let driverNumberDriven = '';
                let driverRating = '';
                let driverReviews = '';
                let confirmedPassengers = 0;

                for (let j = 0; j < users.length; j++) {
                    confirmedPassengers += users[j].accepted ? 1 : 0;

                    if (users[j].driver) {
                        const userId = users[j].userId;
                        const driver = window.base.rest.getUser(userId);
                        driverFirstName = driver.firstName;
                        driverGender = driver.gender === 0 ? 'Male' : 'Female';

                        let today = new Date();
                        let dob = new Date(driver.dateOfBirth);
                        driverAge = today.getFullYear() - dob.getFullYear();
                        let m = today.getMonth() - dob.getMonth();
                        if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                            driverAge--;
                        }

                        driverNumberDriven = window.rest.getNumberOfDrivesForUser(userId);
                        driverReviews = driver.numberOfRatings;
                        driverRating = parseFloat(driver.ratingTotalScore / driverReviews).toFixed(2);
                    }
                }

                const driveNumberOfSeatsLeft = carNumberOfSeats - confirmedPassengers;
                const tripStart = model.searchQuery.tripStart;
                const tripStop = model.searchQuery.tripStop;

                let passengerIcons = '';

                for (let i = 0; i < carNumberOfSeats; i++) {
                    passengerIcons += i < driveNumberOfSeatsLeft ?
                        '<i class="fas fa-user-check text-info"></i>' :
                        '<i class="fas fa-user-check text-muted"></i>'
                }

                // language=HTML
                searchResults += `<div class="row border bg-white shadow-sm">\n            <div class="col-2 border-right">\n                <h5 class="mt-3 mb-0 text-muted font-weight-bold">${driverFirstName}</h5>\n                <p class="text-muted">${driverGender}, ${driverAge}</p>\n                <div class="row mt-4">\n                    <div class="col-2">\n                        <p class="mb-0 text-muted"><i class="fas fa-car fa-sm"></i></p>\n                        <p class="text-muted mb-0"><i class="fas fa-star fa-sm"></i></p>\n                        <p class="text-muted"><i class="fas fa-chart-bar fa-sm"></i></p>\n                    </div>\n                    <div class="col-9">\n                        <p class="text-muted mb-0">${driverNumberDriven} driven</p>\n                        <p class="text-muted mb-0">${driverRating} rating</p>\n                        <p class="text-muted">${driverReviews} reviews</p>\n                    </div>\n                </div>\n            </div>\n            <div class="col-5 border-right">\n                <table class="h-100 w-100">\n                    <tr>\n                        <td class="align-top">\n                            <a href="#/drive">\n                                <h5 class="mt-3 mb-0 text-danger font-weight-bold">${driveName}</h5>     \n                            </a>\n                            <div class="row">\n                                <div class="col-3">\n                                        <p class="text-muted">Leaving:</p>\n                                </div>\n                                <div class="col-9">\n                                    <p class="text-muted">${departureTime}</p>\n                                </div>\n                            </div>\n                        </td>\n                    </tr>\n                    <td class="align-bottom">\n                        <div class="row mb-0">\n                            <div class="col-3">\n                                <p class="text-muted mb-0">Pickup:</p>\n                                <p class="text-muted">Dropoff:</p>\n                            </div>\n                            <div class="col-9">\n                                <p class="text-muted mb-0">${tripStart}</p>\n                                <p class="text-muted">${tripStop}</p>\n                            </div>\n                        </div>\n                    </td>\n                </table>\n            </div>\n            <div class="col-3 border-right">\n                <table class="h-100 w-100">\n                    <tr>\n                        <td class="align-top">\n                            <h5 class="mt-3 mb-0 text-muted font-weight-bold">${carBrand} ${carModel}</h5>\n                            <p class="text-muted mb-0">${carYear} | ${carLicensePlate}</p>\n                        </td>\n                    </tr>\n                    <td class="align-bottom">\n                        <p class="text-muted mb-0">Vehicle preferences</p>\n                        <p>\n                            <i class="fas fa-suitcase fa-lg ${optLuggage}"></i>\n                            <i class="fas fa-snowflake fa-lg ${optWinterTires}"></i>\n                            <i class="fas fa-bicycle fa-lg ${optBicycle}"></i>\n                            <i class="fas fa-paw fa-lg ${optPets}"></i>\n                        </p>\n                    </td>\n                </table>\n            </div>\n            <div class="col-2">\n                <table class="h-100 w-100">\n                    <tr>\n                        <td class="align-top">\n                            <h5 id="drive-seats-left" class="mt-3 text-muted font-weight-bold">${driveNumberOfSeatsLeft} seats left</h5>\n                            ${passengerIcons}\n                        </td>\n                    </tr>\n                    <td class="align-bottom">\n                        <button class="mb-3 btn btn-danger btn-sm btn-block request-btn">Request</button>\n                    </td>\n                </table>`


            }

            searchResultsWrapper.innerHTML = searchResults;
        }
    };

    const controller = {
        submitSearch: e => {
            e.preventDefault();

            const start = document.getElementById('search-start').value;
            const stop = document.getElementById('search-stop').value;
            const departureTime = new Date(document.getElementById('search-departure-time').value).getTime();

            model.searchQuery = {tripStart: start, tripStop: stop, tripDepartureTime: departureTime};

            const userId = model.user.userId;

            controller.performSearch({searchFilterId: 0, userId, start, stop, departureTime})
        },
        performSearch: searchFilter => {
            window.base.rest.searchDrives(searchFilter).then(drives => {
                if (drives.error) {
                    view.showFailure(drives.message);
                } else {
                    model.searchResults = drives;
                    view.renderSearchResults();
                }
            });
        },
        getUser: () => {
            window.base.rest.getUser().then(u => {
                model.user = u;
            });
        },
        load: () => {
            document.getElementById('drive-search').onsubmit = controller.submitSearch;
            controller.getUser();

            // Perform empty search
            controller.performSearch({searchFilterId: 0, userId: 0, start: null, stop: null, departureTime: 0});
        }
    };

    return controller;
});