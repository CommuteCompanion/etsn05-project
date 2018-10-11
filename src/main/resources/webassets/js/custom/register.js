window.base = window.base || {};

window.base.changeLocation = url => window.location.replace(url);

window.base.registerController = (() => {
    const controller = {
        submitUser: submitEvent => {
            submitEvent.preventDefault();

            const password = document.getElementById('register-password').value;
            const email = document.getElementById('register-email').value;
            const firstName = document.getElementById('register-first-name').value;
            const lastName = document.getElementById('register-last-name').value;
            const phoneNumber = document.getElementById('register-phone-number').value;
            const dateOfBirth = Date.parse(document.getElementById('register-date-of-birth').value);
            const drivingLicence = document.getElementById('register-driving-license').value;
            const gender = document.getElementById('register-gender-male').checked ? 0 : 1;

            const user = {email, firstName, lastName, phoneNumber, dateOfBirth, drivingLicence, gender};
            const role = "USER";
            const credentials ={email, password, role, user};

            window.base.rest.addUser(credentials).then(user => {
                if (user.error) {
                    alert(user.message);
                } else {
                    window.base.rest.login(email, password, false).then(response => {
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

        load: () => {
            document.getElementById('register-form').onsubmit = controller.submitUser;
        },
        initOnLoad: () => document.addEventListener('DOMContentLoaded', window.base.registerController.load)
    };
    
    return controller;
})();
