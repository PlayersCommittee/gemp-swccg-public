const MUTED_USER_LIST_DIALOG = 'mutedUserListDialog';
const MUTED_USER_NAME_INPUT = 'muted-username';
const MUTED_REASON_INPUT = 'muted-reason';
const ADD_MUTED_USER_BUTTON = 'add-muted-user';
const UNMUTE_USER_BUTTON = 'unmute-action';

const MUTED_USER_LIST_DIALOG_SELECTOR = `#${MUTED_USER_LIST_DIALOG}`;
const MUTED_USER_NAME_INPUT_SELECTOR = `#${MUTED_USER_NAME_INPUT}`;
const MUTED_REASON_INPUT_SELECTOR = `#${MUTED_REASON_INPUT}`;
const ADD_MUTED_USER_BUTTON_SELECTOR = `#${ADD_MUTED_USER_BUTTON}`;
const UNMUTE_USER_BUTTON_SELECTOR = `.${UNMUTE_USER_BUTTON}`;

const mutedUsersDialog = `
<div id="muted-users" title="Muted Users">
    <div id="muted-user-adder">
        <input  type="text" id="${MUTED_USER_NAME_INPUT}" 
            placeholder="Username"
            autocomplete="off" />
        <input type="text" id="${MUTED_REASON_INPUT}"
            placeholder="Reason"
            autocomplete="off" />
        <button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" 
            id="${ADD_MUTED_USER_BUTTON}"
            disabled
            role="button">Mute User</button>
    </div>
    <h3>Muted Users</h3>
    <div id="muted-user-table-container">
        <table id="muted-user-table">
            <thead>
                <th>Username</th>
                <th>Reason</th>
                <th>Actions</th>
            </thead>
            <tbody id="muted-users-table-body"></tbody>
        </table>
    </div>
</div>
`;

const mutedUserRow = (user) => `<tr><td>${user.name}</td><td>${user.reason}</td><td><button class="${UNMUTE_USER_BUTTON} ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" data-attr-user-name="${user.name}" role="button">Unmute User</button></td></tr>`;

function buildMutedUsersDialog() {
    $(MUTED_USER_LIST_DIALOG_SELECTOR).html(mutedUsersDialog);
    $(MUTED_USER_LIST_DIALOG_SELECTOR).dialog({
        autoOpen: false,
        width: 500,
        title: 'Muted Users',
        zIndex: 10000,
        buttons: {
            "Close": function () {
                $(this).dialog("close");
            }
        }
    });

    addMuteDialogEventListeners();
}

function resetMutedDialogInput() {
    $(MUTED_USER_NAME_INPUT_SELECTOR).val(''),
    $(MUTED_REASON_INPUT_SELECTOR).val('')
}


function showMutedUsersDialog() {
    var mutedUsers = getMutedUsers();
    $('#muted-users-table-body').html('');
    for (var i = 0; i < mutedUsers.length; i++) {
        var user = mutedUsers[i];
        $('#muted-users-table-body').append(mutedUserRow(user));
    }
    $(UNMUTE_USER_BUTTON_SELECTOR).click(function () {
        const userName = $(this).data('attr-user-name');
        removeMutedUser(userName);
        $(this).closest('tr').remove();
    });
    resetMutedDialogInput();
    $(MUTED_USER_LIST_DIALOG_SELECTOR).dialog("open");

}

function addMuteDialogEventListeners() {
    $(ADD_MUTED_USER_BUTTON_SELECTOR).click(function () {
        const user = {
            name: $(MUTED_USER_NAME_INPUT_SELECTOR).val(),
            reason: $(MUTED_REASON_INPUT_SELECTOR).val()
        } 
        const isNewlyMuted = addMutedUser(user);
        if (isNewlyMuted) {
            $('#muted-users-table-body').append(mutedUserRow(user));
            resetMutedDialogInput();
        }
    });

    $(`#muted-user-adder input`).keyup(function (e) {
        let isValid = false;
        const userNameToMute = $(MUTED_USER_NAME_INPUT_SELECTOR).val();
        if (userNameToMute.length > 0 && !isUserMuted(userNameToMute)) {
            isValid = true;
        }

        $(ADD_MUTED_USER_BUTTON_SELECTOR).prop('disabled', !isValid);
        if (e.keyCode === 13 && isValid) {
            $(ADD_MUTED_USER_BUTTON_SELECTOR).click();
        }
    });
}