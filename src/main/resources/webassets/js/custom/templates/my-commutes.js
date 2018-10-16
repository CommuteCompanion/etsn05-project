window.base = window.base || {};

/* TODO:
Complete renderTab
 - complete design
 - get users for rating
 - set driveUser object on show drive
 - implement cancel seat
 - implement edit drive transfer
 - sort drives when they come in
 */


window.base.myCommutesController = (() => {
    const model = {
        user: {},
        driveWraps: [],
        driveUser: {}
    };

    const view = {
        showFailure: msg => {
            document.getElementById('commutes').innerHTML = `<div class="row">\n                        <div class="col-12">\n                            <h5 class="text-muted font-weight-bold">Ooops!</h5>\n                            <p class="text-muted">Something went wrong, error message: ${msg}.</p>\n                        </div>\n                    </div>`;
        },
        renderTab: () => {
            let commutesHtml = '';
            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

            for (let i = 0; i < model.driveWraps.length; i++) {
                const drive = model.driveWraps[i].drive;
                const driveId = drive.driveId;
                const driveName = drive.start + ' to ' + drive.stop;
                const dtd = new Date(drive.departureTime);

                let departureTime = months[dtd.getMonth()] + ' ';
                departureTime += dtd.getDate() + ' at ';
                departureTime += dtd.getHours() + ':' + dtd.getMinutes();

                const users = model.driveWraps[i].users;
                let userIsDriver = false;

                for (let j = 0; j < users.length; j++) {
                    if (users[j].userId === model.user.userId && users[j].driver) {
                        userIsDriver = true;
                    }
                }

                const milestones = model.driveWraps[i].milestones;
                milestones.push({milestone: drive.start, departureTime: drive.departureTime});

                let tripStartTime = '';

                for (let j = 0; j < milestones.length; j++) {
                    if (tripStart.toLowerCase().trim() === milestones[j].milestone.toLowerCase().trim()) {
                        const milestoneDepartureTime = new Date(milestones[j].departureTime);
                        tripStartTime += milestoneDepartureTime.getHours() + ':' + milestoneDepartureTime.getMinutes();
                    }
                }


                let ratings = '<h5 class="text-muted font-weight-bold">Drive not completed</h5>';
                if (drive.arrivalTime > Date.now()) {
                    ratings = `
                    <div class="rating">
                        <input type="button" name="rating" value="5" /><label for="star5" title="5 stars">5 stars</label>
                        <input type="button" name="rating" value="4" /><label for="star4" title="4 stars">4 stars</label>
                        <input type="button" name="rating" value="3" /><label for="star3" title="3 stars">3 stars</label>
                        <input type="button" name="rating" value="2" /><label for="star2" title="2 stars">2 stars</label>
                        <input type="button" id="star1" name="rating" value="1" /><label for="star1" title="S">1 star</label>
                    </div>`
                }

                let buttons = '<button class="mb-3 btn btn-danger btn-sm btn-block view-btn">View</button>'

                if (userIsDriver) {
                    buttons += '<button class="mb-3 btn btn-danger btn-sm btn-block edit-btn">Edit</button>'
                }

                commutesHtml += `
        <div class="row mb-3 border bg-white shadow-sm">
            <div class="col-5 border-right">
                <a class="drive-link" id="drive-${driveId}" href="">
                    <h5 class="mt-3 mb-0 text-danger font-weight-bold">${driveName}</h5>     
                </a>
                <div class="row">
                    <div class="col-3">
                            <p class="text-muted mb-0">Leaving:</p>
                            <p class="text-muted mb-0">Pickup:</p>
                            <p class="text-muted">Dropoff:</p>
                    </div>
                    <div class="col-9">
                        <p class="text-muted">${departureTime}</p>
                        <p class="text-muted mb-0"><span>${tripStart}</span> (~<span>${tripStartTime}</span>)</p>
                        <p class="text-muted">${tripStop}</p>
                    </div>
                </div>
            </div>
            <div class="col-4">${ratings}</div>
            <div class="col-3">${buttons}</div>
        </div>`
            }

            if (commutesHtml.length === 0) {
                commutesHtml = `<div class="row">\n                        <div class="col-12">\n                            <h5 class="text-muted font-weight-bold">No drives found</h5>\n                        </div>\n                    </div>`
            }

            document.getElementById('commutes').innerHTML = commutesHtml;

            const driveLinks = document.getElementsByClassName('drive-link');
            const requestButtons = document.getElementsByClassName('request-btn');

            for (let i = 0; i < driveLinks.length; i++) {
                const link = driveLinks[i];
                const requestButton = requestButtons[i];

                link.onclick = e => {
                    e.preventDefault();
                    model.searchQuery.driveId = link.id.split('-')[1];
                    const milestones = link
                        .parentElement.parentElement
                        .nextElementSibling
                        .children[0].children[0].children[1].children;
                    model.searchQuery.tripStart = milestones[0].children[0].textContent;
                    model.searchQuery.tripStartTime = milestones[0].children[1].textContent;
                    model.searchQuery.tripStop = milestones[1].textContent;
                    controller.goToDrive();
                };

                requestButton.onclick = e => {
                    e.preventDefault();
                    link.click();
                };
            }
        }

    };

    const controller = {
        view,
        getUser: () => window.base.rest.getUser().then(user => model.user = user),
        load: () => {
            controller.getUser()
                .then(() => controller.getDrivesForUser())
                .then(() => view.renderTab('drives'))
                .catch(e => view.showFailure(e.message));
        },
        getDrivesForUser: () => window.base.rest.getDrivesForUser(model.user.id)
            .then(driveWraps => model.driveWraps = driveWraps),
        goToDrive: () => {
            fetch('templates/drive.html')
                .then(response => response.text())
                .then(tabHtml => {
                    document.getElementById('main-tab').innerHTML = tabHtml;
                    window.base.driveController().loadQuery(model.driveUser);
                })
        },
    };

    return controller;
});