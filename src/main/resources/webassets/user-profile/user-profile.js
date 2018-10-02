var base = base || {};
base.userProfileController = (function() {
    var model = {
        user: {}
    };
    var view = {
        render: function() {
            base.rest.getUser().then(function(u){
                model.user = u;
                return u;
            }).then(function() {
                document.getElementById('set-username').value = model.user.username;
            });  
        },
        resetEdit: function() {
            view.render();
            document.getElementById('set-password').value = '';
            document.getElementById('set-password-confirm').value = '';
        }
    };
    var controller = {
        deleteUser: function() {
            base.rest.deleteUser(model.user.id).then(function() {
                base.rest.logout();
            });
        },
        submitUser: function(submitEvent) {
            submitEvent.preventDefault;
            var username = document.getElementById('set-username').value;
            var password = document.getElementById('set-password').value;
            var repeatPassword = document.getElementById('set-password-confirm').value;
            var id = model.user.id;
            var role = model.user.role.name;
            credentials = {username, password, role};
            if (password === '') {
                delete credentials.password;
            }
            if (password === repeatPassword) {
                base.rest.putUser(id, credentials).then(function(user) {
                    if (user.error) {
                        alert(user.message);
                    } else {
                        view.render();
                    }
                });
            } else {
                alert('Passwords don\'t match');
            }
            return false;
        },
        load: function() {
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#reset-user').onclick = view.resetEdit;
            document.querySelector('#delete-account').onclick = controller.deleteUser;
            view.render();
        },
    };

    return controller;
});