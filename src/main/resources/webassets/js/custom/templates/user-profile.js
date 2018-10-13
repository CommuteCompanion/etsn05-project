window.base = window.base || {};

window.base.userProfileController = (() => {
    const model = {
        user: {}
    };

    const view = {
        render: () => window.base.rest.getUser().then(u => {
            model.user = u;
            console.log(u);
            return u;
        }).then(() => {
            document.getElementById('set-email').value = model.user.email; document.getElementById('set-firstname').value = model.user.firstName;
            document.getElementById('set-lastname').value = model.user.firstName;
            if(model.user.gender == 1) {
                document.getElementById('set-female').checked = true;
            } else if (model.user.gender == 0){
                document.getElementById('set-male').checked = true;
            };
            if(model.user.drivingLicence == true) {
                document.getElementById('set-licence-true').checked = true;
            } else if (model.user.drivingLicence == false){
                document.getElementById('set-licence-false').checked = true;
            };
        }),
        clearChanges: () => {
            view.render();
            document.getElementById('set-password').value = '';
            document.getElementById('set-password-confirm').value = '';
        }
    };
    const controller = {
        deleteUser: () => window.base.rest.deleteUser(model.user.userId)
        .then(window.base.rest.logout())
        .then(() => window.location.replace('/')),
        submitUser: submitEvent => {
            submitEvent.preventDefault();

            const email = document.getElementById('set-email').value;
            const firstName = document.getElementById('set-firstname').value;
            const lastName = document.getElementById('set-lastname').value;
            if(document.getElementById('set-male').checked) {
                var gender = 0;
            } else if (document.getElementById('set-female').checked) {
                var gender = 1;
            };
            if(document.getElementById('set-licence-true').checked) {
                var drivingLicence = true;
            } else if (document.getElementById('set-licence-false').checked) {
                var drivingLicence = false;
            };
            //model.user.firstName = firstName;
            //model.user.lastName = lastName;
            //model.user.gender = gender;
            //model.user.drivingLicence = drivingLicence;
            console.log(model.user);
            const password = document.getElementById('set-password').value;
            const repeatPassword = document.getElementById('set-password-confirm').value;
            const id = model.user.userId;
            const role = model.user.role.name;
            const updatedUser = model.user;
            
            const credentials = {email, password, role, updatedUser};
            if (password === '') {
                delete credentials.password;
            }
            if (password === repeatPassword) {
                window.base.rest.putUser(id, {email, password, role, updatedUser}).then(user => {
                    if (user.error) {
                        view.render();
                        alert(user.message);
                    } else {
                        view.render();
                        document.getElementById('email').textContent = email;
                    }
                });
            } else {
                alert('Passwords don\'t match');
            }
        },
        load: () => {
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#clear-changes').onclick = view.clearChanges;
            document.querySelector('#delete-account').onclick = controller.deleteUser;
            view.render();
        },
    };

    return controller;
});