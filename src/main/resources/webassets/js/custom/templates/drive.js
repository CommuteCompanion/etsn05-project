window.base = window.base || {};

window.base.driveController = (() => {
    const model = {
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
        submitRequest: e => {
            e.preventDefault();

            const driveId = 0;
            const userId = 0;
            const start = '';
            const stop = '';
            const driver = false;
            const accepted = false;
            const rated = false;
            const comment = document.getElementById('request-comment').value;

            const driveUser = {driveId, userId, start, stop, driver, accepted, rated, comment};

            window.base.rest.requestSeat(driveUser)
                .catch(e => {
                    view.showFailure(e.message);
                })
                .then(() => {
                    view.showSuccess();
                })
        },
        load: searchQuery => {
            if(typeof searchQuery === 'undefined') {
                window.base.changeLocation('#/search');
            }

            model.driveUser.driveId = searchQuery.driveId;
            model.driveUser.start = searchQuery.tripStart;
            model.driveUser.stop = searchQuery.tripStop;

            document.getElementById('drive-request').onsubmit = controller.submitRequest;
        },
        initOnLoad: () => document.addEventListener('DOMContentLoaded', window.base.driveController.load())
    };

    return controller;
});