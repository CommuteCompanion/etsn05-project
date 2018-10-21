window.base = window.base || {};

window.base.reportedDrivesController = (() => {
    const model = {
        drives: [],
    };

    const view = {
        getDriverUserId: function (drive) {
            drive.users.forEach(user => {
                if (user.driver === true) {
                    return user.userId;
                }
            });
        },

        getRenderedRating: function (user) {
            if (user.numberOfRatings !== 0) {
                return 'Rating' + parseFloat(user.ratingTotalScore / user.numberOfRatings).toFixed(2);
            }

            return 'No rating';
        },

        renderCard: function (drive) {
            drive.reports.forEach(report => {
                const t = document.getElementById('reported-drives-template');
                let reporter;
                let driver;

                const p1 = window.base.rest.getUser(report.reportedByUserId)
                    .then(user => {
                        reporter = user;
                    });


                const p2 = window.base.rest.getUser(view.getDriverUserId(drive))
                    .then(user => {
                        driver = user;
                    });

                Promise.all([p1, p2]).then(() => {
                    const dt = new Date(drive.drive.departureTime);

                    const year = dt.getFullYear();
                    let month = dt.getMonth();
                    let date = dt.getDate();
                    let hour = dt.getHours();
                    let minute = dt.getMinutes();
                    month = month < 10 ? '0' + month : month;
                    date = date < 10 ? '0' + date : date;
                    hour = hour < 10 ? '0' + hour : hour;
                    minute = minute < 10 ? '0' + minute : minute;
                    const formattedDate = year + '-' + month + '-' + date + ' ' + hour + ':' + minute;

                    t.content.querySelector('.report-start-destination-time').textContent = drive.drive.start + ' to ' + drive.drive.stop + ', ' + formattedDate;
                    t.content.querySelector('.report-driver').textContent = 'Driver: ' + driver.firstName + ' ' + driver.lastName;
                    t.content.querySelector('.report-driver-rating-warning').textContent = view.getRenderedRating(driver) + ' | ' + driver.warning + ' warnings';
                    t.content.querySelector('.report-driver-rating-warning').id = 'report-driver-rating-warning' + report.reportId;
                    t.content.querySelector('.report-reported-by').textContent = 'Reported by: ' + reporter.firstName + ' ' + reporter.lastName;
                    t.content.querySelector('.report-reported-by-rating-warning').textContent = view.getRenderedRating(reporter) + ' | ' + reporter.warning + ' warnings';
                    t.content.querySelector('.report-reported-by-rating-warning').id = 'report-reported-by-rating-warning' + report.reportId;
                    t.content.querySelector('.report-comment').textContent = report.reportMessage;

                    // Associate buttons correct Id
                    t.content.querySelector('.dismiss-report-button').id = 'dismiss-report-button' + report.reportId;
                    t.content.querySelector('.warn-driver-button').id = 'warn-driver-button' + report.reportId;
                    t.content.querySelector('.warn-reporter-button').id = 'warn-reporter-button' + report.reportId;
                    t.content.querySelector('.remove-driver-button').id = 'remove-driver-button' + report.reportId;
                    t.content.querySelector('.remove-reporter-button').id = 'remove-reporter-button' + report.reportId;
                    t.content.querySelector('.delete-drive-button').id = 'delete-drive-button' + report.reportId;

                    // Associate card with correct id
                    t.content.querySelector('.reported-drives').id = 'reported-drives' + report.reportId;

                    const clone = document.importNode(t.content, true);
                    t.parentElement.appendChild(clone);

                    document.getElementById('dismiss-report-button' + report.reportId).onclick = () => controller.dismissReport(report);
                    document.getElementById('warn-driver-button' + report.reportId).onclick = () => controller.giveWarningDriver(report, driver);
                    document.getElementById('warn-reporter-button' + report.reportId).onclick = () => controller.giveWarningReporter(report, reporter);
                    document.getElementById('remove-driver-button' + report.reportId).onclick = () => controller.deleteDriver(report, driver);
                    document.getElementById('remove-reporter-button' + report.reportId).onclick = () => controller.deleteReporter(report, reporter);
                    document.getElementById('delete-drive-button' + report.reportId).onclick = () => controller.deleteDrive(drive);
                });

            });
        },

        render: () => {
            model.drives.forEach(drive => view.renderCard(drive));
        },
    };

    const controller = {
        dismissReport: report => {
            const item = document.getElementById('reported-drives' + report.reportId);
            item.parentElement.removeChild(item);
        },

        giveWarningDriver: (report, user) => window.base.rest.warnUser(user.userId)
            .then(() => {
                user.warning = user.warning + 1;
                document.getElementById('report-driver-rating-warning' + report.reportId).textContent = view.getRenderedRating(user) + ' | ' + user.warning + ' warnings';
            }),

        giveWarningReporter: (report, user) => window.base.rest.warnUser(user.userId)
            .then(() => {
                user.warning = user.warning + 1;
                document.getElementById('report-reported-by-rating-warning' + report.reportId).textContent = view.getRenderedRating(user) + ' | ' + user.warning + ' warnings';
            }),

        deleteDriver: (report, user) => window.base.rest.deleteUser(user.userId)
            .then(function () {
                // TODO: Change to alert box
                alert('Driver deleted!');
            }),

        deleteReporter: (report, user) => window.base.rest.deleteUser(user.userId)
            .then(function () {
                // TODO: Change to alert box
                alert('Reporter deleted!');
            }),

        deleteDrive: report => window.base.rest.deleteDrive(report.drive.driveId)
            .then(() => {
                const item = document.getElementById('reported-drives' + report.reportId);
                item.parentElement.removeChild(item);
            }),

        load: () => window.base.rest.getReportedDrives()
            .then(drives => model.drives = drives)
            .then(() => view.render())
    };

    return controller;
});