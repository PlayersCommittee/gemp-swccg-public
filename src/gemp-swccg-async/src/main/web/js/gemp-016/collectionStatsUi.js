var CollectionStatsUI = Class.extend({
    communication:null,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.loadCollectionStats();
    },

    loadCollectionStats:function () {
        var that = this;
        this.communication.getCollectionStats(
            function (xml) {
                that.loadedCollectionStats(xml);
            });
    },

    loadedCollectionStats:function (xml) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'playerCollectionStats') {
            $("#playerCollectionStats").html("");

            var stats = root;

            var allCards = stats.getElementsByTagName("all")[0];

            $("#playerCollectionStats").append("<div class='playerStatHeader'>Set completion</div>");
            this.appendStats(allCards);
        }
    },

    appendStats:function (stats) {
        var entries = stats.getElementsByTagName("entry");
        if (entries.length == 0) {
            $("#playerCollectionStats").append("<i>Unknown</i>");
        } else {
            var table = $("<table class='tables'></table>");
            table.append("<tr><th>Set</th><th>Available</th><th>Collected</th><th>Missing</th><th>Foils collected</th></tr>");
            for (var i = 0; i < entries.length; i++) {
                var entry = entries[i];

                table.append("<tr><td>" + entry.getAttribute("set") + "</td><td>" + entry.getAttribute("available") + "</td><td>" + entry.getAttribute("collected") + "</td><td>" + entry.getAttribute("missing") + "</td><td>" + entry.getAttribute("foil")  + "</td></tr>");
            }

            $("#playerCollectionStats").append(table);
        }
    }
});