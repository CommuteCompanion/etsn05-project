window.base = window.base || {};
window.base.driveController = (() => {
    var controller = {
        load: () => {
            //To be added
        },
        initOnLoad: () => {
            document.addEventListener('DOMContentLoaded', window.base.driveController.load);
        }
    };
    return controller;
});