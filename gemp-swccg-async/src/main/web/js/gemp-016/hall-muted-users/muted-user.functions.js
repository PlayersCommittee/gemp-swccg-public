const IS_MUTE_ENABLED = "isMuteEnabled";
const MUTED_USERS = "mutedUsers";

// type MutedUser = {
//     name: string;
//     reason: string;
// }

const USER_NAME_REGEX = new RegExp(/<b>(\S*):<\/b>/);

function getIsMuteEnabled() {
    return window.localStorage.getItem(IS_MUTE_ENABLED) ?? false;
}

function getMutedUsers() {
    return JSON.parse(window.localStorage.getItem(MUTED_USERS)) ?? [];
}

function setMutedUsers(userNames) {
    window.localStorage.setItem(MUTED_USERS, JSON.stringify(userNames));
}

function isUserMuted(userName) {
    const mutedUsers = getMutedUsers();
    return mutedUsers.find(user => user.name === userName);
}

function addMutedUser({name, reason = ''}) {
    const mutedUsers = getMutedUsers();
    if (isUserMuted(name)) {
        return false;
    }
    setMutedUsers([...mutedUsers, { name, reason }]);
    return true;
}

function getUserNameFromMessage(message) {
    const matches = USER_NAME_REGEX.exec(message);
    if (matches === null) {
        return '';
    }
    return matches[1];
}

function checkIfMessageShouldBeMuted(message) {
    if (window.location.href.includes("game.html")) {
        return false;
    }
    const userName = getUserNameFromMessage(message);
    return !!isUserMuted(userName);
}