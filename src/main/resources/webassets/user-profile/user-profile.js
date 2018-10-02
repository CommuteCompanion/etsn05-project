var base = base || {};
base.userProfileController = (function() {
    var model = {
        user: {}
    };
    var view = {
        render: function() {
            document.getElementById("set-username").value = model.user.username;
        }
    };
    var controller = {
        deleteUser: function() {
            console.log(model.user.id);
            console.log(model.user.username);
            console.log(model.user);
            base.rest.deleteUser(model.user.id).then(function() {
                base.rest.logout();
            });
        },
        submitUser: function(submitEvent) {
            console.log(model.user);
        },
        load: function() {
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#delete-account').onclick = controller.deleteUser;

            base.rest.getUser().then(function(u){
                model.user = u;
                return u;
            }).then(function() {
                view.render();
            });  
        },
    };

    return controller;
});