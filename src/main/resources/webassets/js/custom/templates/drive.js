window.base = window.base || {};

window.base.driveController = (() => {
    const model = {
        user: {},
        driveUser: {}
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
        }
    };

    const controller = {
        submitRequest: () => window.base.rest.requestSeat(model.driveUser)
            .catch(e => view.showFailure(e.message))
            .then(() => view.showSuccess()),
        getUser: () => window.base.rest.getUser().then(u => {
            model.user = u;
            model.driveUser.userId = u.userId;
        }),
        load: searchQuery => {
            if (typeof searchQuery === 'undefined') {
                window.base.changeLocation('#/search');
            }

            model.driveUser = {
                driveId: searchQuery.driveId,
                start: searchQuery.tripStart,
                stop: searchQuery.tripStop,
                driver: false,
                accepted: false,
                rated: false
            };

            document.getElementById('drive-request').onclick = controller.submitRequest
        },
    };

    return controller;
})
;