var base = base || {};
base.registerController = (function() {
    var controller = {
        load: function() {
            console.log("Hello!");
        },
        initOnLoad: function() {
            document.addEventListener('DOMContentLoaded', base.registerController.load);
        }
    };
    
    return controller;
})();
