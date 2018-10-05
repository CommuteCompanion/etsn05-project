var base = base || {};

const BASE_PATH = "templates/";

base.mainController = (() => {
    const routingTable = {
        'search': {
            partial: BASE_PATH + 'search.html',
            controller: base.searchController
        },
        'create-drive': {
            partial: BASE_PATH + 'create-drive.html',
            controller: base.createDriveController
        },
        'edit-drive': {
            partial: BASE_PATH + 'edit-drive.html',
            controller: base.editDriveController
        },
        'drive': {
            partial: BASE_PATH + 'drive.html',
            controller: base.driveController
        },
        'my-commutes': {
            partial: BASE_PATH + 'my-commutes.html',
            controller: base.myCommutesController
        },
        'manage-users': {
            partial: BASE_PATH + 'manage-users.html',
            controller: base.manageUsersController
        },
        'reported-drives': {
            partial: BASE_PATH + 'reported-drives.html',
            controller: base.reportedDrivesController
        },
        'user-profile': {
            partial: BASE_PATH + 'user-profile.html',
            controller: base.userProfileController
        },
    };

    const model = {
        route: ''
    };

    const view = {
        render: () => {
            const nav = document.getElementById('main-nav');
            const activeTabLink = nav.querySelector('li.active');
            if (activeTabLink) activeTabLink.classList.remove('active');
            const newActiveTabLink = nav.querySelector('a[href="#/'+model.route+'"]');
            if (newActiveTabLink) newActiveTabLink.parentElement.classList.add('active');
        },
        hideAdminLinks: () => document.querySelectorAll('#main-nav li.admin-only').forEach(li => li.style.display = 'none'),
        hideUserLinks: () => document.querySelectorAll('#main-nav li.user-only').forEach(li => li.style.display = 'none'),
        renderUsername: () => document.getElementById('username').textContent = model.user.username
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
            view.render();
        },
        load: () => {
            document.getElementById('logout').onclick = controller.logout;
            window.onhashchange = base.mainController.changeRoute;
            base.mainController.changeRoute();
            base.rest.getUser().then(user => {
                model.user = user;
                view.renderUsername();
                if (user.isNone()) {
                    base.changeLocation('/login.html');
                } else if (!user.isAdmin()) {
                    view.hideAdminLinks();
                } else {
                    view.hideUserLinks();
                }
            });
        },
        logout: () => base.rest.logout().then(() => base.changeLocation('/login.html')),
        initOnLoad: () => document.addEventListener("DOMContentLoaded", base.mainController.load)
    };
    return controller;
})();

base.changeLocation = url => window.location.replace(url);
