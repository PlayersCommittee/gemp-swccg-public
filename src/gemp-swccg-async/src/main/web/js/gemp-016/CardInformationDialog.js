var CardInformationDialog = Class.extend({
    infoDialog: null,

    init:function () {
        this.infoDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            title:"Card information"
        });
    },

    showCardInfo: function(blueprintId, testingText, backSideTestingText) {
        var card = new Card(blueprintId, testingText, backSideTestingText);

        this.infoDialog.html("");
        this.infoDialog.html("<div style='scroll: auto'></div>");
        this.infoDialog.append(createFullCardDiv(card.imageUrl, card.testingText, card.foil, card.horizontal, card.isPack()));
        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = 30;
        var vertSpace = 45;

        if (card.horizontal) {
            // 500x360
            this.infoDialog.dialog({width:Math.min(500 + horSpace, windowWidth), height:Math.min(380 + vertSpace, windowHeight)});
        } else {
            // 360x500
            this.infoDialog.dialog({width:Math.min(360 + horSpace, windowWidth), height:Math.min(520 + vertSpace, windowHeight)});
        }
        this.infoDialog.dialog("open");
    },

    isOpened: function() {
        return this.infoDialog.dialog("isOpen");
    },

    close: function() {
        this.infoDialog.dialog("close");
    }
});