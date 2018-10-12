window.base = window.base || {};

window.base.rest = (() => {
    function Role(role) {
        this.name = role;
        this.label = this.name[0] + this.name.toLowerCase().slice(1);
    }

    function User(json) {
        Object.assign(this, json);
        this.role = new Role(json.role);
        this.json = json;
        this.isAdmin = () => this.role.name === 'ADMIN';
        this.isNone = () => this.role.name === 'NONE';
    }

    function Drive(json) {
        Object.assign(this, json) ;
    }

    const objOrError = (json, cons) => json.error ? json : new cons(json);

    window.base.User = User;
    window.base.Role = Role;

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

    return {
        getReportedDrives: () => baseFetch('/rest/drive/all')
            .then(response => response.json())
            .then(drives => drives.map(d => new Drive(d))),

        putDrive: (id, drive) => baseFetch('/rest/drive/' + id, {
            method: 'PUT',
            body: JSON.stringify(drive),
            headers: jsonHeader
        }),

        addDrive: (driveWrap) => baseFetch('/rest/drive/', {
            method: 'post',
            body: JSON.stringify(driveWrap),
            headers: jsonHeader
        })
        .then(response => response.json())
        .then(d => objOrError(d, Drive)),

        getUser: () => baseFetch('/rest/user')
            .then(response => response.json())
            .then(u => new User(u)),
        login: (email, password, rememberMe) => baseFetch('/rest/user/login?remember=' + rememberMe, {
            method: 'POST',
            body: JSON.stringify({email: email, password: password}),
            headers: jsonHeader
        }),
        logout: () => baseFetch('/rest/user/logout', {method: 'POST'}),
        getUsers: () => baseFetch('/rest/user/all')
            .then(response => response.json())
            .then(users => users.map(u => new User(u))),
        getRoles: () => baseFetch('/rest/user/roles')
            .then(response => response.json())
            .then(roles => roles.map(r => new Role(r))),
        addUser: credentials => baseFetch('/rest/user', {
            method: 'POST',
            body: JSON.stringify(credentials),
            headers: jsonHeader
        })
            .then(response => response.json())
            .then(u => objOrError(u, User)),
        putUser: (id, credentials) => baseFetch('/rest/user/' + id, {
            method: 'PUT',
            body: JSON.stringify(credentials),
            headers: jsonHeader
        })
            .then(response => response.json())
            .then(u => objOrError(u, User)),
        deleteUser: id => baseFetch('/rest/user/' + id, {method: 'DELETE'})
    };
})();

