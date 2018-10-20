window.base = window.base || {};

/* TODO:
 - rate users
 - implement cancel seat
 */

window.base.myCommutesController = (() => {
    const model = {
        user: {},
        driveWraps: []
    };

    const view = {
        renderAlertBox: (element, title, message, type) => {
            element.innerHTML = `<div class="alert alert-${type}" role="alert">\n                    <h5 class="alert-heading">${title}</h5>\n                    <p>${message}</p>\n                </div>`;
        },

        renderPage: driveWraps => {
            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            let commutesHtml = '';
            let drivesFound = true;
            let firstDrive = true;
            let firstTrip = true;

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
                let ratingBox = `<form class="rate-form" id="form-drive-${driveId}"><div id="alert-drive-${driveId}"></div>`;
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
                            actionButtons += `<button class="btn btn-danger btn-sm btn-block cancel-btn" id="cancel-trip-${driveId}">Cancel Seat</button>`;
                        }
                    }

                    if (users[j].driver) {
                        driver = users[j];
                    }
                }

                // Set correct headline for drives
                if (currentUser.isDriver && firstDrive === true) {
                    firstDrive = false;
                    commutesHtml += '<div class="row"><div class="col-12"><h5 class="text-muted font-weight-bold">My Drives</h5></div></div>';
                } else if (firstTrip === true) {
                    firstTrip = false;
                    commutesHtml += '<div class="row"><div class="col-12"><h5 class="text-muted font-weight-bold">My Trips</h5></div></div>';
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
                    ratingBox += '<h5 class="text-muted font-weight-bold">Please rate trip</h5>';

                    // HTML partial -> rating forms
                    if (currentUser.isDriver) {
                        for (let i = 1; i <= users.length; i++) {
                            const firstName = users[i - 1].info.firstName;
                            ratingBox += `<div class="form-group">\n                            <label for="user-rating-${driveId}-${i}" class="sr-only">${firstName}</label>\n                            <select class="form-control rate-select form-control-sm" id="user-rating-${driveId}-${i}">\n                              <option disabled selected>${firstName}</option>\n                              <option>1</option>\n                              <option>2</option>\n                              <option>3</option>\n                              <option>4</option>\n                              <option>5</option>\n                            </select>\n                          </div>`;
                        }
                    } else {
                        const firstName = driver.info.firstName;
                        ratingBox += `<div class="form-group">\n                            <label for="user-rating-${driveId}-1" class="sr-only">${firstName}</label>\n                            <select class="form-control rate-select form-control-sm" id="user-rating-${driveId}-1">\n                              <option disabled selected>${firstName}</option>\n                              <option>1</option>\n                              <option>2</option>\n                              <option>3</option>\n                              <option>4</option>\n                              <option>5</option>\n                            </select>\n                          </div>`;
                    }

                    ratingBox += `<button type="submit" class="btn btn-secondary btn-block btn-sm">Rate</button>`;
                } else {
                    ratingBox += '<h5 class="text-muted font-weight-bold">Drive not completed</h5>';
                }

                // HTML partial -> end rating form
                ratingBox += '</form>';

                // HTML partial -> drive row
                commutesHtml += `\n        <div class="row mb-3 border bg-white shadow-sm pt-3 pb-3">\n            <div class="col-7 border-right">\n                <a class="view-link" id="view-drive-${driveId}" href="">\n                    <h5 class="mb-0 text-danger font-weight-bold">${driveName}</h5>     \n                </a>\n                <div class="row">\n                    <div class="col-3">\n                            <p class="text-muted mb-0">Leaving:</p>\n                            <p class="text-muted mb-0">Pickup:</p>\n                            <p class="text-muted">Dropoff:</p>\n                    </div>\n                    <div class="col-9">\n                        <p class="text-muted mb-0">${departureTime}</p>\n                        <p class="text-muted mb-0"><span>${tripStart}</span> (~<span>${tripStartTime}</span>)</p>\n                        <p class="text-muted">${tripStop}</p>\n                    </div>\n                </div>\n            </div>\n            <div class="col-3 border-right">${ratingBox}</div>\n            <div class="col-2">${actionButtons}</div>\n        </div>`
            }

            // HTML partial -> not drives found
            if (commutesHtml.length === 0) {
                drivesFound = false;
                commutesHtml = `<div class="row">\n                        <div class="col-12">\n                            <h5 class="text-muted font-weight-bold">No drives found</h5>\n                        </div>\n                    </div>`
            }

            // Render page
            document.getElementById('commutes').innerHTML = commutesHtml;

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

                    // For clicking on a view button
                    cancelButton.onclick = e => {
                        e.preventDefault();
                        const driveId = cancelButton.id.split('-')[2];
                        controller.cancelTrip(driveId);
                    };

                    if (typeof editButton !== 'undefined') {
                        // For clicking on an edit button
                        editButton.onclick = e => {
                            e.preventDefault();
                            const driveId = editButton.id.split('-')[2];
                            controller.editDrive(driveId);
                        };
                    }

                    // For submit user ratings
                    rateForm.addEventListener('submit', e => {
                        e.preventDefault();
                        const driveId = rateForm.id.split('-')[2];
                        let i = 1;
                        let ratings = [];

                        while (typeof document.getElementById('user-rating-' + driveId + '-' + i) !== 'undefined') {
                            const input = document.getElementById('user-rating-' + driveId + '-' + i);
                            const userId = input.options[input.selectedIndex].getAttribute('data-user-id');
                            const rating = input.options[input.selectedIndex].value;

                            ratings = {userId, rating};

                            i++;
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
            .then(() => view.renderAlertBox('alert-drive-' + driveId, 'Done', 'Thank you for your rating', 'info'))
            .then(e => view.renderAlertBox('alert-drive-' + driveId, 'Oops!', 'Something went wrong, ' + e.message, 'danger')),

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
                // TODO: fix alert box
                //.catch(view.renderError);
        },
    };

    return controller;
});