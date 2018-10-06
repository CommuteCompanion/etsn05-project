window.base = window.base || {};

window.base.changeLocation = url => window.location.replace(url);

window.base.registerController = (() => {
    const controller = {
        submitUser: submitEvent => {
            submitEvent.preventDefault();
            const password = document.getElementById('register-password').value;
            const username = document.getElementById('register-username').value;
            const role = "USER";
            const credentials ={username, password, role};
            window.base.rest.addUser(credentials).then(user => {
                if (user.error) {
                    alert(user.message);
                } else {
                    window.base.rest.login(username, password, false).then(response => {
                        if (response.ok) {
                            window.base.changeLocation('/');
                        } else {
                            alert('Error during login.');
                        }
                    });
                }
            });

            return false;
        },

        load: () => document.getElementById('register-form').onsubmit = controller.submitUser,
        initOnLoad: () => document.addEventListener('DOMContentLoaded', window.base.registerController.load)
    };
    
    return controller;
})();
