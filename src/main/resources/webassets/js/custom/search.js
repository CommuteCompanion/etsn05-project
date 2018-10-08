window.base = window.base || {};
window.base.searchController = (() => {
    var controller = {
        load: () => {
            //To be done
        },
        initOnLoad: () => {
            document.addEventListener('DOMContentLoaded', window.base.searchController.load);
        }
    };
    return controller;
});