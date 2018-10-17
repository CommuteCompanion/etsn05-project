window.base = window.base || {};

window.base.userProfileController = (() => {
    const model = {
        user: {},
        userId: '',
    };

    const view = {
        pad (number) {
            if (number < 10) {
              return '0' + number;
            }
            return number;
          },
        render: () => window.base.rest.getUser(model.userId).then(u => {
            model.user = u;
            return u;
        }).then(() => {
            document.getElementById('set-email').value = model.user.email;
            document.getElementById('set-firstname').value = model.user.firstName;
            document.getElementById('set-lastname').value = model.user.lastName;
            document.getElementById('set-phone-number').value = model.user.phoneNumber;
            var birthDate = new Date(model.user.dateOfBirth);
            document.getElementById('set-date-of-birth').value 
                = birthDate.getUTCFullYear() + '-' + view.pad(birthDate.getUTCMonth() + 1 ) + '-' + view.pad(birthDate.getUTCDay());
            if (model.user.gender === 1) {
                document.getElementById('set-female').checked = true;
            } else if (model.user.gender === 0) {
                document.getElementById('set-male').checked = true;
            }
            if (model.user.drivingLicence === true) {
                document.getElementById('set-licence-true').checked = true;
            } else if (model.user.drivingLicence === false) {
                document.getElementById('set-licence-false').checked = true;
            }
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

            let gender;
            let drivingLicence;
            const email = document.getElementById('set-email').value;
            const firstName = document.getElementById('set-firstname').value;
            const lastName = document.getElementById('set-lastname').value;
            //const phoneNumber = document.getElementById('set-phone-number').value;
            //const birthDate = document.getElementById('set-date-of-birth').value;
            if(document.getElementById('set-male').checked) {
                gender = 0;
            } else if (document.getElementById('set-female').checked) {
                gender = 1;
            }
            if (document.getElementById('set-licence-true').checked) {
                drivingLicence = true;
            } else if (document.getElementById('set-licence-false').checked) {
                drivingLicence = false;
            }
            model.user.firstName = firstName;
            model.user.lastName = lastName;
            model.user.gender = gender;
            model.user.drivingLicence = drivingLicence;
            model.user.phoneNumber = phoneNumber;
            model.user.dateOfBirth = birthDate;

            const password = document.getElementById('set-password').value;
            const repeatPassword = document.getElementById('set-password-confirm').value;
            const id = model.user.userId;
            const role = model.user.role.name;
            const roleObj = model.user.role;
            model.user.role = role;
            const user = model.user;

            const credentials = {email, password, role, user};
            if (password === '') {
                delete credentials.password;
            }
            if (password === repeatPassword) {
                window.base.rest.putUser(id, {email, password, role, user}).then(user => {
                    if (user.error) {
                        view.render();
                        alert(user.message);
                    } else {
                        document.getElementById('email').innerText = email;
                        view.render();
                    }
                });
            } else {
                alert('Passwords don\'t match');
            }
            model.user.role = roleObj;
        },
        load: () => {
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#clear-changes').onclick = view.clearChanges;
            document.querySelector('#delete-account').onclick = controller.deleteUser;
            view.render();
        },
        loadWithUserId: (id) => {
            model.userId = id;
            controller.load();
        },
    };

    return controller;
});
