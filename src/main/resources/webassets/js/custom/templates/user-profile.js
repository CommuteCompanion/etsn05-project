var base = base || {};
base.userProfileController = (() => {
    const model = {
        user: {}
    };

    const view = {
        render: () => base.rest.getUser().then(u => {
                model.user = u;
                return u;
            }).then(() => document.getElementById('set-username').value = model.user.username),
        clearChanges: () => {
            view.render();
            document.getElementById('set-password').value = '';
            document.getElementById('set-password-confirm').value = '';
        }
    };
    const controller = {
        deleteUser: () => base.rest.deleteUser(model.user.userId)
                .then(base.rest.logout())
                .then(() => window.location.replace('/')),
        submitUser: submitEvent => {
            submitEvent.preventDefault();
            const username = document.getElementById('set-username').value;
            const password = document.getElementById('set-password').value;
            const repeatPassword = document.getElementById('set-password-confirm').value;
            const id = model.user.userId;
            const role = model.user.role.name;
            const credentials = {username, password, role};
            if (password === '') {
                delete credentials.password;
            }
            if (password === repeatPassword) {
                base.rest.putUser(id, {username, password, role}).then(user => {
                    if (user.error) {
                        alert(user.message);
                    } else {
                        view.render();
                        document.getElementById('username').textContent = username;
                    }
                });
            } else {
                alert('Passwords don\'t match');
            }
            return false;
        },
        load: () => {
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#clear-changes').onclick = view.clearChanges;
            document.querySelector('#deconste-account').onclick = controller.deleteUser;
            view.render();
        },
    };

    return controller;
});