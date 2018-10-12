window.base = window.base || {};

window.base.changeLocation = url => window.location.replace(url);

window.base.loginController = (() => {
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
            window.base.rest.getUser().then(user => {
                if (!user.isNone()) {
                    window.base.changeLocation('/');
                }
            });
        },
        loginUser: () => {
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const remember = document.getElementById('remember').checked;
            window.base.rest.login(email, password, remember)
                .then(response => {
                    if (response.ok) {
                        window.base.changeLocation('/');
                    } else {
                        document.getElementById('password').value = '';
                        response.json().then(error => view.showFailure(error.message));
                    }
                });
        },
        initOnLoad: () => {
            document.addEventListener('DOMContentLoaded', window.base.loginController.load);
        }
    };

    return controller;
})();
