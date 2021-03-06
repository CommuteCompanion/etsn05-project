window.base = window.base || {};

window.base.myCommutesController = (() => {
    const model = {
        user: {},
        driveWraps: []
    };

    const view = {
        renderAlertBox: (element, message, type) => {
            element.innerHTML = `<div class="alert alert-${type}" role="alert"><span>${message}</span></div>`;
        },

        renderError: e => {
            document.getElementById('commutes').innerHTML = `<div class="row">\n                        <div class="col-12">\n                            <h5 class="text-muted font-weight-bold">Ooops!</h5>\n                            <p class="text-muted">Something went wrong, error message: ${e.message}.</p>\n                        </div>\n                    </div>`;
        },

        renderPage: driveWraps => {
            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            let commutesHtml = '';
            let drivesFound = true;

            for (let i = 0; i < driveWraps.length; i++) {
                // Basic data
                const drive = driveWraps[i].drive;
                const driveId = drive.driveId;
                const driveName = drive.start + ' to ' + drive.stop;

                // Departure time
                const dtd = new Date(drive.departureTime);
                let departureTime = months[dtd.getMonth()] + ' ' + dtd.getDate() + ' at ' + controller.parseTime(dtd);

                // Milestones
                const milestones = driveWraps[i].milestones;
                milestones.push({milestone: drive.start, departureTime: drive.departureTime});
                let tripStart;
                let tripStartTime;
                let tripStop;

                // Users
                const users = driveWraps[i].users;
                let currentUser;
                let driver;

                // HTML partials
                let ratingBox = '';
                let actionButtons = '<button class="mb-3 btn btn-info btn-sm btn-block view-btn">View</button>';

                // Get current drive user and set correct buttons
                for (let j = 0; j < users.length; j++) {
                    if (users[j].userId === model.user.userId) {
                        currentUser = users[j];
                        currentUser.isDriver = false;

                        if (users[j].driver) {
                            driver = users[j];
                            currentUser.isDriver = true;
                            actionButtons += `<button class="btn btn-danger btn-sm btn-block edit-btn" id="edit-drive-${driveId}">Edit</button>`;
                        } else {
                            if (Date.now() < drive.departureTime) {
                                if (users[j].accepted) {
                                    actionButtons += `<button class="btn btn-danger btn-sm btn-block cancel-btn" id="cancel-trip-${driveId}">Cancel Seat</button>`;
                                } else {
                                    actionButtons += `<button class="btn btn-danger btn-sm btn-block cancel-btn" id="cancel-trip-${driveId}">Cancel Request</button>`;
                                }
                            }
                        }
                    }

                    if (users[j].driver) {
                        driver = users[j];
                    }
                }

                // Set correct pickup and drop off points
                tripStart = currentUser.start;
                tripStop = currentUser.stop;

                // Get correct pickup time
                for (let j = 0; j < milestones.length; j++) {
                    if (currentUser.start === milestones[j].milestone) {
                        const pickupDeparture = new Date(milestones[j].departureTime);
                        tripStartTime = controller.parseTime(pickupDeparture);
                    }
                }

                // HTML partial -> rate box title
                if (Date.now() > drive.arrivalTime) {
                    ratingBox += `<div id="alert-drive-${driveId}"></div><form class="rate-form" id="form-drive-${driveId}"><h5 class="text-muted font-weight-bold">Please rate trip for</h5>`;

                    // HTML partial -> rating forms
                    if (currentUser.isDriver) {
                        for (let i = 1; i <= users.length; i++) {
                            const userId = users[i - 1].info.userId;
                            if (userId !== model.user.userId) {
                                const firstName = users[i - 1].info.firstName;
                                ratingBox += `<div class="form-group row mb-0">\n                                    <label for="user-rating-${driveId}-${i}" class="col-6 col-form-label">${firstName}</label>\n                                    <div class="col-6">\n                                      <select class="form-control rate-select form-control-sm" id="user-rating-${driveId}-${i}" data-user-id="${userId}">\n                                          <option>5</option>\n                                          <option>4</option>\n                                          <option>3</option>\n                                          <option>2</option>\n                                          <option>1</option>\n                                      </select>\n                                    </div>\n                                </div>`;
                            }
                        }
                    } else {
                        const userId = driver.info.userId;
                        const firstName = driver.info.firstName;
                        ratingBox += `<div class="form-group row mb-0">\n                                    <label for="user-rating-${driveId}-1" class="col-6 col-form-label">${firstName}</label>\n                                    <div class="col-6">\n                                      <select class="form-control rate-select form-control-sm" id="user-rating-${driveId}-1" data-user-id="${userId}">\n                                          <option>5</option>\n                                          <option>4</option>\n                                          <option>3</option>\n                                          <option>2</option>\n                                          <option>1</option>\n                                      </select>\n                                    </div>\n                                </div>`;
                    }

                    ratingBox += `<button type="submit" class="mt-2 btn btn-secondary btn-block btn-sm">Rate</button></form>`;
                } else {
                    ratingBox += '<h5 class="text-muted font-weight-bold">Drive not completed</h5>';
                }

                // HTML partial -> drive row
                commutesHtml = `\n        <div class="row mb-3 border bg-white shadow-sm pt-3 pb-3">\n            <div class="col-7 border-right">\n                <a class="view-link" id="view-drive-${driveId}" href="">\n                    <h5 class="mb-0 text-danger font-weight-bold">${driveName}</h5>     \n                </a>\n                <div class="row">\n                    <div class="col-3">\n                            <p class="text-muted mb-0">Leaving:</p>\n                            <p class="text-muted mb-0">Pickup:</p>\n                            <p class="text-muted">Dropoff:</p>\n                    </div>\n                    <div class="col-9">\n                        <p class="text-muted mb-0">${departureTime}</p>\n                        <p class="text-muted mb-0"><span>${tripStart}</span> (~<span>${tripStartTime}</span>)</p>\n                        <p class="text-muted">${tripStop}</p>\n                    </div>\n                </div>\n            </div>\n            <div class="col-3 border-right">${ratingBox}</div>\n            <div class="col-2">${actionButtons}</div>\n        </div>`
                if (currentUser.isDriver) {
                    document.getElementById('my-drives').innerHTML += commutesHtml;
                } else {
                    document.getElementById('my-trips').innerHTML += commutesHtml;
                }
            }

            // HTML partial -> not drives found
            if (commutesHtml.length === 0) {
                drivesFound = false;
                commutesHtml = `<div class="row">\n                        <div class="col-12">\n                            <h5 class="text-muted font-weight-bold">No drives found</h5>\n                        </div>\n                    </div>`
                document.getElementById('commutes').innerHTML = commutesHtml;
            }

            // Add logic if drives were rendered
            if (drivesFound) {
                const viewLinks = document.getElementsByClassName('view-link');
                const viewButtons = document.getElementsByClassName('view-btn');
                const editButtons = document.getElementsByClassName('edit-btn');
                const cancelButtons = document.getElementsByClassName('cancel-btn');
                const rateForms = document.getElementsByClassName('rate-form');

                for (let i = 0; i < viewLinks.length; i++) {
                    const viewLink = viewLinks[i];
                    const viewButton = viewButtons[i];
                    const editButton = editButtons[i];
                    const cancelButton = cancelButtons[i];
                    const rateForm = rateForms[i];

                    // For clicking on a headline link
                    viewLink.onclick = e => {
                        e.preventDefault();
                        const milestones = viewLink.nextElementSibling.children[1].children;
                        const selection = {
                            driveId: viewLink.id.split('-')[2],
                            tripStart: milestones[1].children[0].textContent,
                            tripStartTime: milestones[1].children[1].textContent,
                            tripStop: milestones[2].textContent
                        };

                        controller.viewDrive(selection);
                    };

                    // For clicking on a view button
                    viewButton.onclick = e => {
                        e.preventDefault();
                        viewLink.click();
                    };

                    if (typeof cancelButton !== 'undefined') {
                        // For clicking on a view button
                        cancelButton.onclick = e => {
                            e.preventDefault();
                            const driveId = cancelButton.id.split('-')[2];
                            const col = cancelButton.parentElement;
                            const row = col.parentElement

                            col.classList.add('pt-4','pb-4');
                            col.innerHTML = '<div class="loader"></div>';

                            controller.cancelTrip(row, driveId);
                        };

                    }

                    if (typeof editButton !== 'undefined') {
                        // For clicking on an edit button
                        editButton.onclick = e => {
                            e.preventDefault();
                            const driveId = editButton.id.split('-')[2];
                            controller.editDrive(driveId);
                        };
                    }

                    if (typeof rateForm !== 'undefined') {
                        // For submit user ratings
                        rateForm.addEventListener('submit', e => {
                            e.preventDefault();
                            const driveId = parseInt(rateForm.id.split('-')[2]);
                            let noUsers = 0;
                            let ratings = [];

                            for (let i = 0; i < model.driveWraps.length; i++) {
                                if (model.driveWraps[i].drive.driveId === driveId) {
                                    noUsers = model.driveWraps[i].users.length;
                                }
                            }

                            for (let i = 1; i <= noUsers; i++) {
                                if (document.getElementById('user-rating-' + driveId + '-' + i) !== null) {
                                    const input = document.getElementById('user-rating-' + driveId + '-' + i);
                                    const ratedUserId = parseInt(input.getAttribute('data-user-id'));
                                    const rating = parseInt(input.options[input.selectedIndex].value);

                                    ratings.push({ratedUserId, rating});
                                }
                            }

                            const ratingWrap = {
                                userId: model.user.userId,
                                driveId: driveId,
                                ratings
                            };

                            controller.rateDrive(driveId, ratingWrap);
                        });
                    }
                }
            }
        }
    };

    const controller = {
        // Rest calls
        getUser: () => window.base.rest.getUser()
            .then(user => {
                model.user = user;
                return user.userId;
            }),

        getUserById: userId => window.base.rest.getUser(userId),

        getDrivesForUser: userId => window.base.rest.getDrivesForUser(userId),

        // Ingoing logic
        assignUsersToDrives: driveWraps =>
            Promise.all(driveWraps.map(driveWrap =>
                Promise.all(driveWrap.users.map(({userId}) =>
                    controller.getUserById(userId)))
                    .then(users => users.map((user, i) =>
                        Object.assign({info: user}, driveWrap.users[i])))
                    .then(users => {
                        model.driveWraps.push(Object.assign(driveWrap, {users}));
                        return 0;
                    })
            )),

        filterDrives: driveWraps => driveWraps.filter(driveWrap => {
            const users = driveWrap.users;
            for (let i = 0; i < users.length; i++) {
                if (users[i].userId === model.user.userId && users[i].rated === true) {
                    return false;
                }
            }

            return true;
        }),

        sortDrives: driveWraps => driveWraps.sort((a, b) => {
            let isDriverA = false;
            let isDriverB = false;

            for (let i = 0; i < a.users.length; i++) {
                if (a.users.userId === model.user.userId && a.users.driver === true) {
                    isDriverA = true;
                }
            }

            for (let i = 0; i < b.users.length; i++) {
                if (b.users.userId === model.user.userId && b.users.driver === true) {
                    isDriverB = true;
                }
            }

            if (isDriverA && isDriverB) {
                return a.departureTime - b.departureTime;
            }

            return isDriverA ? -1 : 1;
        }),

        // Outgoing logic
        cancelTrip: (wrapper, driveId) => window.base.rest.removeUserFromDrive(driveId, model.user.userId)
            .then(() => wrapper.remove()),

        rateDrive: (driveId, ratingWrap) => window.base.rest.rateDrive(driveId, ratingWrap)
            .then(rating => {
                if (rating.error) {
                    throw rating;
                }

                const alertBox = document.getElementById('alert-drive-' + driveId);
                view.renderAlertBox(alertBox, 'Thank you for your rating', 'info');
                alertBox.nextElementSibling.classList.add('d-none');
            })
            .catch(e => view.renderAlertBox(document.getElementById('alert-drive-' + driveId), 'Something went wrong, ' + e.message, 'danger')),

        parseTime: date => {
            let hours = date.getHours();
            let minutes = date.getMinutes();
            hours = hours < 10 ? '0' + hours : hours;
            minutes = minutes < 10 ? '0' + minutes : minutes;

            return hours + ':' + minutes;
        },

        viewDrive: selection => {
            fetch('templates/drive.html')
                .then(response => response.text())
                .then(tabHtml => {
                    document.getElementById('main-tab').innerHTML = tabHtml;
                    window.base.driveController().loadQuery(selection);
                })
        },

        editDrive: driveId => {
            fetch('templates/edit-drive.html')
                .then(response => response.text())
                .then(tabHtml => {
                    document.getElementById('main-tab').innerHTML = tabHtml;
                    window.base.editDriveController().loadWithUserId(driveId);
                })
        },

        load: () => {
            controller.getUser()
                .then(controller.getDrivesForUser)
                .then(controller.filterDrives)
                .then(controller.sortDrives)
                .then(controller.assignUsersToDrives)
                .then(() => view.renderPage(model.driveWraps))
                .catch(view.renderError);
        },
    };

    return controller;
});