window.base = window.base || {};

window.base.manageUsersController = (() => {
    var model = {
        users: []
    };

    var view = {
        renderCard: function(user) {
            var t = document.getElementById('manage-users-template');
            
            t.content.querySelector('.admin-name').textContent = 
                user.firstName + ' ' + user.lastName;
            t.content.querySelector('.admin-email').textContent = 
                user.email;
            if (user.numberOfRatings === 0) {
                t.content.querySelector('.admin-rating').textContent = 
                    'No rating yet';
            } else {
                t.content.querySelector('.admin-rating').textContent = 
                    user.ratingTotalScore/user.numberOfRatings + 'rating';
            }
            if (user.warning == null) {
                user.warning = 0;
            }
            t.content.querySelector('.admin-warning').textContent = 
                user.warning + ' warnings';

            // Associate buttons and cards with correct Id
            t.content.querySelector('.edit-profile-button').id   = 'edit-profile-' + user.userId;
            t.content.querySelector('.give-warning-button').id   = 'give-warning-' + user.userId;
            t.content.querySelector('.delete-account-button').id = 'delete-account-' + user.userId;
            
            t.content.querySelector('.manage-users-user').id = 'manage-user-card-' + user.userId;

            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);

            // Add functionality to buttons.
            document.getElementById('edit-profile-' + user.userId).onclick = () =>
                controller.editProfile(user)
            document.getElementById('give-warning-' + user.userId).onclick = () =>
                controller.giveWarning(user)
            document.getElementById('delete-account-' + user.userId).onclick = () =>
                controller.deleteAccount(user)
        },
        render: function() {
            model.users.forEach(user => view.renderCard(user));
        },
    };

    const controller = {
        giveWarning: function(user) {
            window.base.rest.warnUser(user.userId);
        },
        deleteAccount: function(user) {
            window.base.rest.deleteUser(user.userId).then(function () {
                var item = document.getElementById('manage-user-card-' + user.userId);
                item.parentElement.removeChild(item);
            }); 
            
        },
        editProfile: function(user) {
            console.log('EDIT!! ID: ' + user.userId);
        },
        load: function() {
            base.rest.getUsers().then(function(users) {
                model.users = users;
                return users;
            }).then(function() {
                view.render();
            });
        }
    };

    return controller;
});