window.base = window.base || {};

window.base.userProfileController = (() => {
    const model = {
        user: {}
    };

    const view = {
        render: () => window.base.rest.getUser().then(u => {
                model.user = u;
                return u;
        }).then(() => document.getElementById('set-email').value = model.user.email),
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
            const password = document.getElementById('set-password').value;
            const repeatPassword = document.getElementById('set-password-confirm').value;
            const id = model.user.userId;
            const role = model.user.role.name;
            const credentials = {email, password, role};
            if (password === '') {
                delete credentials.password;
            }
            if (password === repeatPassword) {
                window.base.rest.putUser(id, {email, password, role}).then(user => {
                    if (user.error) {
                        alert(user.message);
                    } else {
                        view.render();
                        document.getElementById('email').textContent = email;
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
            document.querySelector('#delete-account').onclick = controller.deleteUser;
            view.render();
        },
    };

    return controller;
});