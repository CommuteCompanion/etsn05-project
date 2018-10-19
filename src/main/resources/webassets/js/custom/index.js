window.base = window.base || {};
window.base.changeLocation = url => window.location.replace(url);

window.base.mainController = (() => {
    const BASE_PATH = "templates/";

    const routingTable = {
        'search': {
            partial: BASE_PATH + 'search.html',
            controller: window.base.searchController
        },
        'edit-drive': {
            partial: BASE_PATH + 'edit-drive.html',
            controller: window.base.editDriveController
        },
        'drive': {
            partial: BASE_PATH + 'drive.html',
            controller: window.base.driveController
        },
        'my-commutes': {
            partial: BASE_PATH + 'my-commutes.html',
            controller: window.base.myCommutesController
        },
        'manage-users': {
            partial: BASE_PATH + 'manage-users.html',
            controller: window.base.manageUsersController
        },
        'reported-drives': {
            partial: BASE_PATH + 'reported-drives.html',
            controller: window.base.reportedDrivesController
        },
        'user-profile': {
            partial: BASE_PATH + 'user-profile.html',
            controller: window.base.userProfileController
        },
    };

    const model = {
        route: '',
        user: {}
    };

    const view = {
        updateNavBarLinks: () => {
            const nav = document.getElementById('main-nav');
            const activeTabLink = nav.querySelector('li.active');
            const newActiveTabLink = nav.querySelector('a[href="#/'+model.route+'"]');

            if (activeTabLink) {
                activeTabLink.classList.remove('active')
            }

            if (newActiveTabLink) {
                newActiveTabLink.parentElement.classList.add('active')
            }
        },
        hideAdminLinks: () =>
            document.querySelectorAll('#main-nav li.admin-only').forEach(li => li.style.display = 'none'),

        hideUserLinks: () =>
            document.querySelectorAll('#main-nav li.user-only').forEach(li => li.style.display = 'none'),

        renderNavbarInfo: () => {
            document.getElementById('navbar-first-name').textContent = model.user.firstName;
            const reviews = model.user.numberOfRatings;
            const score = model.user.ratingTotalScore;
            document.getElementById('navbar-user-rating').textContent = reviews === 0 ? 'N/A' : parseFloat(score / reviews).toFixed(2);
        },

        hideRating: () => document.getElementById('navbar-user-rating').parentElement.style.display = 'none'
    };

    const controller = {
        routingTable: routingTable,

        changeRoute: () => {
            const newRoute = location.hash.slice(2);

            if (!controller.routingTable[newRoute]) {
                location.hash = '/'+Object.keys(controller.routingTable)[0];
                return;
            }

            model.route = newRoute;

            fetch(controller.routingTable[newRoute].partial)
                .then(response => response.text())
                .then(tabHtml => {
                document.getElementById('main-tab').innerHTML = tabHtml;
                controller.routingTable[newRoute].controller().load();
            });

            view.updateNavBarLinks();
        },

        load: () => {
            window.base.rest.getUser()
                .then(user => {
                if (user.isNone()) {
                    window.base.changeLocation('/login.html');
                } else {
                    model.user = user;

                    document.getElementsByTagName('html')[0].classList.remove('d-none');
                    document.getElementById('logout').onclick = controller.logout;

                    view.renderNavbarInfo();

                    window.onhashchange = window.base.mainController.changeRoute;
                    window.base.mainController.changeRoute();

                    if (user.isAdmin()) {
                        view.hideUserLinks();
                        view.hideRating();
                    } else {
                        view.hideAdminLinks();
                    }
                }
            });
        },

        logout: () => window.base.rest.logout()
            .then(() => window.base.changeLocation('/login.html')),

        initOnLoad: () => document.addEventListener("DOMContentLoaded", window.base.mainController.load)
    };

    return controller;
})();