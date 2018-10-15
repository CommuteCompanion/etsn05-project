window.base = window.base || {};

window.base.driveController = (() => {
    const controller = {
        submitRequest: e => {
            e.preventDefault();

            const comment = document.getElementById('request-comment').value;
        },
        load: () => {
            document.getElementById('drive-request').onsubmit = controller.submitRequest;
        }
    };

    return controller;
});