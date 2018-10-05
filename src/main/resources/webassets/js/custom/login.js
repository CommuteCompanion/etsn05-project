var base = base || {};

base.changeLocation = url => window.location.replace(url);

base.loginController = (() => {
    const view = {
        showFailure: msg => alert(msg)
    };

    const controller = {
        view,
        load: () => {
            document.getElementById('login-form').onsubmit = event => {
                event.preventDefault();
                controller.loginUser();
                return false;
            };
            base.rest.getUser().then(user => {
                if (!user.isNone()) {
                    base.changeLocation('/');
                }
            });
        },
        loginUser: () => {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const remember = document.getElementById('remember').checked;
            base.rest.login(username, password, remember)
                .then(response => {
                    if (response.ok) {
                        base.changeLocation('/');
                    } else {
                        document.getElementById('password').value = '';
                        response.json().then(error => view.showFailure(error.message));
                    }
                });
        },
        initOnLoad: () => {
            document.addEventListener('DOMContentLoaded', base.loginController.load);
        }
    };

    return controller;
})();
