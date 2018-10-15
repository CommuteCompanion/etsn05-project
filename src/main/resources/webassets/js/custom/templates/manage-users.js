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
            t.content.querySelector('.admin-warning').id = 'admin-warning' + user.userId;

            // Associate buttons and cards with correct Id
            t.content.querySelector('.edit-profile-button').id   = 'edit-profile' + user.userId;
            t.content.querySelector('.give-warning-button').id   = 'give-warning' + user.userId;
            t.content.querySelector('.delete-account-button').id = 'delete-account' + user.userId;
            
            t.content.querySelector('.manage-users-user').id = 'manage-user-card' + user.userId;

            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);

            // Add functionality to buttons.
            document.getElementById('edit-profile' + user.userId).onclick = () =>
                controller.editProfile(user)
            document.getElementById('give-warning' + user.userId).onclick = () =>
                controller.giveWarning(user)
            document.getElementById('delete-account' + user.userId).onclick = () =>
                controller.deleteAccount(user)
        },
        render: function() {
            model.users.forEach(user => view.renderCard(user));
        },
    };

    const controller = {
        giveWarning: function(user) {
            window.base.rest.warnUser(user.userId).then(function () {
                user.warning = user.warning + 1;
                document.getElementById('admin-warning'+user.userId).textContent 
                    = user.warning + ' warnings';
                });
        },
        deleteAccount: function(user) {
            window.base.rest.deleteUser(user.userId).then(function () {
                var item = document.getElementById('manage-user-card' + user.userId);
                item.parentElement.removeChild(item);
            }); 
            
        },
        editProfile: function(user) {
            fetch('templates/user-profile.html')
                    .then(response => response.text())
                    .then(tabHtml => {
                        document.getElementById('main-tab').innerHTML = tabHtml;
                        window.base.userProfileController().loadWithUserId(user.userId);  
                    });
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
