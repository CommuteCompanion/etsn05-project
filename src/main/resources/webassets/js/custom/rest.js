window.base = window.base || {};

window.base.rest = (() => {
    function Role(role) {
        this.name = role;
        this.label = this.name[0] + this.name.toLowerCase().slice(1);
    }

    function User(json) {
        this.userId = json.userId;
        this.role = new Role(json.role);
        this.email = json.email;
        this.firstName = json.firstName;
        this.lastName = json.lastName;
        this.phoneNumber = json.phoneNumber;
        this.gender = json.gender;
        this.dateOfBirth = json.dateOfBirth;
        this.drivingLicence = json.drivingLicence;
        this.ratingTotalScore = json.ratingTotalScore;
        this.numberOfRatings = json.numberOfRatings;
        this.warning = json.warning;
        this.isAdmin = () => this.role.name === 'ADMIN';
        this.isNone = () => this.role.name === 'NONE';
    }

    function Drive(json) {
        Object.assign(this, json);
        this.json = json;
    }
        
    function DriveWrap(json) {
        this.drive = new Drive(json.drive);
        this.milestones = json.milestones.map(milestone => new DriveMilestone(milestone));
        this.users = json.users.map(user => new DriveUser(user));
    }

    function DriveMilestone(json) {
        this.milestoneId = json.milestoneId;
        this.driveId = json.driveId;
        this.milestone = json.milestone;
        this.departureTime = json.departureTime;
    }

    function DriveUser(json) {
        this.driveId = json.driveId;
        this.userId = json.userId;
        this.start = json.start;
        this.stop = json.stop;
        this.driver = json.driver;
        this.accepted = json.accepted;
        this.rated = json.rated;
    }

    function Drive(json) {
        this.driveId = json.driveId;
        this.carNumberOfSeats = json.carNumberOfSeats;
        this.optLuggageSize = json.optLuggageSize;
        this.start = json.start;
        this.stop = json.stop;
        this.comment = json.comment;
        this.carBrand = json.carBrand;
        this.carModel = json.carModel;
        this.carColor = json.carColor;
        this.carLicensePlate = json.carLicensePlate;
        this.optWinterTires = json.optWinterTires;
        this.optPets = json.optPets;
        this.optBicycle = json.optBicycle;
        this.departureTime = json.departureTime;
        this.arrivalTime = json.arrivalTime;
    }
    function DriveReport(json) {
        Object.assign(this, json);
        this.json = json;
    }

    const objOrError = (json, cons) => json.error ? json : new cons(json);

    window.base.User = User;
    window.base.Role = Role;
    window.base.DriveWrap = DriveWrap;
    window.base.DriveReport = DriveReport;
    window.base.Drive = Drive;
    window.base.DriveMilestone = DriveMilestone;
    window.base.DriveUser = DriveUser;

    const baseFetch = (url, config) => {
        config = config || {};
        config.credentials = 'same-origin';
        return fetch(url, config).catch(error => {
            alert(error);
            throw error;
        });
    };

    const jsonHeader = {
        'Content-Type': 'application/json;charset=utf-8'
    };
    const createJsonPost = body => {
        return {
            method: 'POST',
            body: JSON.stringify(body),
            headers: jsonHeader
        };
    };

    return {
        getReportedDrives: () => baseFetch('/rest/drive/all-reports')
        .then(response => response.json())
        .then(drives => drives.map(d => new DriveReport(d))),

        getDrive: (id) => baseFetch('/rest/user/' + id, {
            method: 'GET',
        })
        .then(response => response.json())
        .then(d => new DriveWrap(d)),

        putDrive: (id, drive) => baseFetch('/rest/drive/' + id, {
            method: 'PUT',
            body: JSON.stringify(drive),
            headers: jsonHeader
        })
        .then(response => response.json())
        .then(d => objOrError(d, DriveWrap)),

        addDrive: (driveWrap) => baseFetch('/rest/drive/', {
            method: 'POST',
            body: JSON.stringify(driveWrap),
            headers: jsonHeader
        })
        .then(response => response.json())
        .then(d => objOrError(d, DriveWrap)),

        addReport: (id, driveReport) => baseFetch('rest/drive/' + id + '/report', {
            method: 'POST',
            body: JSON.stringify(driveReport),
            headers: jsonHeader
        })
        .then(response => response.json())
        .then(d => objOrError(d, DriveReport)),

        searchDrives: searchFilter => baseFetch('/rest/search/drives', createJsonPost(searchFilter))
            .then(response => response.json())
            .then(driveWraps => driveWraps.map(driveWrap => new DriveWrap(driveWrap))),
        getUser: userId => typeof userId === 'undefined' ?
            baseFetch('/rest/user')
                .then(response => response.json())
                .then(u => new User(u)) :
            baseFetch('/rest/user/' + userId)
                .then(response => response.json())
                .then(user => new User(user)),
        getNumberOfDrivesForUser: userId => baseFetch('/rest/drive/count/' + userId)
            .then(response => response.text())
                .then(i => parseInt(i)),
        requestSeat: (userId, driveUser) => baseFetch('/rest/drive/' + userId, createJsonPost(driveUser)),
        login: (email, password, rememberMe) => baseFetch('/rest/user/login?remember=' + rememberMe, createJsonPost({email: email, password: password})),
        logout: () => baseFetch('/rest/user/logout', {method: 'POST'}),

        getUsers: () => baseFetch('/rest/user/all')
            .then(response => response.json())
            .then(users => users.map(user => new User(user))),
        getRoles: () => baseFetch('/rest/user/roles')
            .then(response => response.json())
            .then(roles => roles.map(r => new Role(r))),
        addUser: credentials => baseFetch('/rest/user', createJsonPost(credentials))
            .then(response => response.json())
            .then(u => objOrError(u, User)),
        putUser: (id, credentials) => baseFetch('/rest/user/' + id, createJsonPost(credentials))
            .then(response => response.json())
            .then(u => objOrError(u, User)),
        deleteUser: id => baseFetch('/rest/user/' + id, {method: 'DELETE'}),

        warnUser: (id) => baseFetch('/rest/user/warn/' + id, {
            method: 'PUT',
            headers: jsonHeader
        }),
    };
})();

