var PlayerStatsUI = Class.extend({
    communication:null,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.loadPlayerStats();
    },

    loadPlayerStats:function () {
        var that = this;
        this.communication.getPlayerStats(
            function (xml) {
                that.loadedPlayerStats(xml);
            });
    },

    loadedPlayerStats:function (xml) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'playerStats') {
            $("#playerStats").html("");

            var stats = root;

            var casual = stats.getElementsByTagName("casual")[0];
            var competitive = stats.getElementsByTagName("competitive")[0];

            $("#playerStats").append("<div class='playerStatHeader'>Casual statistics</div>");
            this.appendStats(casual);
            $("#playerStats").append("<div class='playerStatHeader'>Competitive statistics</div>");
            this.appendStats(competitive);
        }
    },

    appendStats:function (stats) {
        var entries = stats.getElementsByTagName("entry");
        if (entries.length == 0) {
            $("#playerStats").append("<i>You have not played any games counting for this statistics</i>");
        } else {
            var table = $("<table class='tables'></table>");
            table.append("<tr><th>Format name</th><th>Deck name</th><th># of wins</th><th># of losses</th><th>% of wins</th></tr>");
            for (var i = 0; i < entries.length; i++) {
                var entry = entries[i];

                table.append("<tr><td>" + entry.getAttribute("format") + "</td><td>" + entry.getAttribute("deckName") + "</td><td>" + entry.getAttribute("wins") + "</td><td>" + entry.getAttribute("losses") + "</td><td>" + entry.getAttribute("perc") + "</td></tr>");
            }

            $("#playerStats").append(table);
        }
    }
});