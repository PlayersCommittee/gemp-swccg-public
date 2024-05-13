var dialogsSized = new Array();

function openSizeDialog(dialog) {
    var sizedDialog = function () {
        for (var i = 0; i < dialogsSized.length; i++)
            if (dialogsSized[i] == dialog)
                return true;
        return false;
    };

    if (!sizedDialog(dialog)) {
        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var dialogWidth = windowWidth * 0.8;
        var dialogHeight = windowHeight * 0.8;

        dialogsSized.push(dialog);
        dialog.dialog({width:dialogWidth, height:dialogHeight});
    }
    dialog.dialog("open");
}
