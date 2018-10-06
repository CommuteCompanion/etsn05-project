var base = base || {};
base.myCommutesController = (function() {
    var controller = {
        load: function() {

        },
        initOnLoad: function() {
            document.addEventListener('DOMContentLoaded', base.myCommutesController.load);
        }
    };
    return controller;
});