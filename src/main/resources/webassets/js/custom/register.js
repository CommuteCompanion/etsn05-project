window.base = window.base || {};

window.base.changeLocation = url => window.location.replace(url);

window.base.registerController = (() => {
    const controller = {
        submitUser: submitEvent => {
            submitEvent.preventDefault();
            const password = document.getElementById('register-password').value;
            const username = document.getElementById('register-username').value;
            const firstName = document.getElementById('register-firstName').value;
            const lastName = document.getElementById('register-lastName').value;
            const phoneNumber = document.getElementById('register-phoneNumber').value;
            const email = document.getElementById('register-email').value;
            const dateOfBirth = document.getElementById('register-dateOfBirth').value;
            const drivingLicence = document.getElementById('register-drivingLicence').checked;

            const user = {firstName, lastName, phoneNumber, email, dateOfBirth, drivingLicence}
            const role = "USER";
            const credentials ={username, password, role, user};
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