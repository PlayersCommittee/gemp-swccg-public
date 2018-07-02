var StatsUI = Class.extend({
    communication:null,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });


        var now = new Date();
        var nowStr = now.getFullYear() + "-" + (1 + now.getMonth()) + "-" + now.getDate();

        $("#statsParameters").append("Start date: <input class='startDay' type='text' value='" + nowStr + "'>");
        $("#statsParameters").append(" period: <select class='period'><option value='month'>month</option><option value='week'>week</option><option value='day'>day</option></select>");
        $("#statsParameters").append(" <input class='getStats' type='button' value='Display stats'>");

        var that = this;

        $(".getStats", $("#statsParameters")).click(
            function () {
                var startDay = $(".startDay", $("#statsParameters")).prop("value");
                var period = $("option:selected", $(".period", $("#statsParameters"))).prop("value");

                that.communication.getStats(startDay, period, that.loadedStats, {
                    "400":function () {
                        alert("Invalid parameter entered");
                    }
                })
            });
    },

    loadedStats:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'stats') {
            $("#stats").html("");

            var stats = root;

            var activePlayers = stats.getAttribute("activePlayers");
            var gamesCount = stats.getAttribute("gamesCount");
            var start = stats.getAttribute("start");
            var end = stats.getAttribute("end");

            $("#stats").append("<div class='period'>Stats for " + start + " - " + end + "</div>");
            $("#stats").append("<div class='activePlayers'>Active players: " + activePlayers + "</div>");
            $("#stats").append("<div class='gamesCount'>All games count: " + gamesCount + "</div>");

            var formatStats = stats.getElementsByTagName("formatStat");
            if (formatStats.length > 0) {
                $("#stats").append("<div class='tableHeader'>Casual games per format</div>");

                var table = $("<table class='tables'></table>");
                table.append("<tr><th>Format name</th><th># of games</th><th>% of casual</th></tr>");
                for (var i = 0; i < formatStats.length; i++) {
                    var formatStat = formatStats[i];
                    table.append("<tr><td>" + formatStat.getAttribute("format") + "</td><td>" + formatStat.getAttribute("count") + "</td><td>" + formatStat.getAttribute("perc") + "</td></tr>");
                }

                $("#stats").append(table);
            }
        }
    }
});