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
                searchResults += `
        <div class="row border bg-white shadow-sm">
            <div class="col-2 border-right">
                <h5 class="mt-3 mb-0 text-muted font-weight-bold">${driverFirstName}</h5>
                <p class="text-muted">${driverGender}, ${driverAge}</p>
                <div class="row mt-4">
                    <div class="col-2">
                        <p class="mb-0 text-muted"><i class="fas fa-car fa-sm"></i></p>
                        <p class="text-muted mb-0"><i class="fas fa-star fa-sm"></i></p>
                        <p class="text-muted"><i class="fas fa-chart-bar fa-sm"></i></p>
                    </div>
                    <div class="col-9">
                        <p class="text-muted mb-0">${driverNumberDriven} driven</p>
                        <p class="text-muted mb-0">${driverRating} rating</p>
                        <p class="text-muted">${driverReviews} reviews</p>
                    </div>
                </div>
            </div>
            <div class="col-5 border-right">
                <table class="h-100 w-100">
                    <tr>
                        <td class="align-top">
                            <a href="#/drive">
                                <h5 class="mt-3 mb-0 text-danger font-weight-bold">${driveName}</h5>     
                            </a>
                            <div class="row">
                                <div class="col-3">
                                        <p class="text-muted">Leaving:</p>
                                </div>
                                <div class="col-9">
                                    <p class="text-muted">${departureTime}</p>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <td class="align-bottom">
                        <div class="row mb-0">
                            <div class="col-3">
                                <p class="text-muted mb-0">Pickup:</p>
                                <p class="text-muted">Dropoff:</p>
                            </div>
                            <div class="col-9">
                                <p class="text-muted mb-0">${tripStart}</p>
                                <p class="text-muted">${tripStop}</p>
                            </div>
                        </div>
                    </td>
                </table>
            </div>
            <div class="col-3 border-right">
                <table class="h-100 w-100">
                    <tr>
                        <td class="align-top">
                            <h5 class="mt-3 mb-0 text-muted font-weight-bold">${carBrand} ${carModel}</h5>
                            <p class="text-muted mb-0">${carYear} | ${carLicensePlate}</p>
                        </td>
                    </tr>
                    <td class="align-bottom">
                        <p class="text-muted mb-0">Vehicle preferences</p>
                        <p>
                            <i class="fas fa-suitcase fa-lg ${optLuggage}"></i>
                            <i class="fas fa-snowflake fa-lg ${optWinterTires}"></i>
                            <i class="fas fa-bicycle fa-lg ${optBicycle}"></i>
                            <i class="fas fa-paw fa-lg ${optPets}"></i>
                        </p>
                    </td>
                </table>
            </div>
            <div class="col-2">
                <table class="h-100 w-100">
                    <tr>
                        <td class="align-top">
                            <h5 id="drive-seats-left" class="mt-3 text-muted font-weight-bold">${driveNumberOfSeatsLeft} seats left</h5>
                            ${passengerIcons}
                        </td>
                    </tr>
                    <td class="align-bottom">
                        <button class="mb-3 btn btn-danger btn-sm btn-block request-btn">Request</button>
                    </td>
                </table>
            </div>
        </div>`

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