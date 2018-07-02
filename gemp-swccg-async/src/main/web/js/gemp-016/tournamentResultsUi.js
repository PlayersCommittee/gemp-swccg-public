var TournamentResultsUI = Class.extend({
    communication:null,
    formatDialog:null,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.formatDialog = $("<div></div>")
            .dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:false,
                modal:true,
                title:"Format description"
            });

        this.loadLiveTournaments();
    },

    loadLiveTournaments:function () {
        var that = this;
        this.communication.getLiveTournaments(
            function (xml) {
                that.loadedTournaments(xml);
            });
    },

    loadHistoryTournaments:function () {
        var that = this;
        this.communication.getHistoryTournaments(
            function (xml) {
                that.loadedTournaments(xml);
            });
    },

    loadedTournament:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'tournament') {
            $("#tournamentExtraInfo").html("");

            var tournament = root;

            var tournamentId = tournament.getAttribute("id");
            var tournamentName = tournament.getAttribute("name");
            var tournamentFormat = tournament.getAttribute("format");
            var tournamentCollection = tournament.getAttribute("collection");
            var tournamentRound = tournament.getAttribute("round");
            var tournamentStage = tournament.getAttribute("stage");

            $("#tournamentExtraInfo").append("<div class='tournamentName'>" + tournamentName + "</div>");
            $("#tournamentExtraInfo").append("<div class='tournamentFormat'><b>Format:</b> " + tournamentFormat + "</div>");
            $("#tournamentExtraInfo").append("<div class='tournamentCollection'><b>Collection:</b> " + tournamentCollection + "</div>");
            if (tournamentStage == "Playing games")
                $("#tournamentExtraInfo").append("<div class='tournamentRound'><b>Round:</b> " + tournamentRound + "</div>");

            var standings = tournament.getElementsByTagName("tournamentStanding");
            if (standings.length > 0)
                $("#tournamentExtraInfo").append(this.createStandingsTable(standings, tournamentId, tournamentStage));
        }
    },

    loadedTournaments:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'tournaments') {
            $("#tournamentResults").html("");

            var tournaments = root.getElementsByTagName("tournament");
            for (var i = 0; i < tournaments.length; i++) {
                var tournament = tournaments[i];
                var tournamentId = tournament.getAttribute("id");
                var tournamentName = tournament.getAttribute("name");
                var tournamentFormat = tournament.getAttribute("format");
                var tournamentCollection = tournament.getAttribute("collection");
                var tournamentRound = tournament.getAttribute("round");
                var tournamentStage = tournament.getAttribute("stage");

                $("#tournamentResults").append("<div class='tournamentName'>" + tournamentName + "</div>");
                $("#tournamentResults").append("<div class='tournamentRound'><b>Round:</b> " + tournamentRound + "</div>");

                var detailsBut = $("<button>See details</button>").button();
                detailsBut.click(
                    (function (id) {
                        return function () {
                            that.communication.getTournament(id,
                                function (xml) {
                                    that.loadedTournament(xml);
                                });
                        };
                    })(tournamentId));
                $("#tournamentResults").append(detailsBut);
            }
            if (tournaments.length == 0)
                $("#tournamentResults").append("<i>There is no running tournaments at the moment</i>");

            $("#tournamentResults").append("<hr />");
            $("#tournamentResults").append("<div id='tournamentExtraInfo'></div>");
        }
    },

    createStandingsTable:function (standings, tournamentId, tournamentStage) {
        var standingsTable = $("<table class='standings'></table>");

        standingsTable.append("<tr><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>Opp. Win %</th><th></th><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>Opp. Win %</th></tr>");

        var secondColumnBaseIndex = Math.ceil(standings.length / 2);

        for (var k = 0; k < secondColumnBaseIndex; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            var playerStr;
            if (tournamentStage == "Finished")
                playerStr = "<a target='_blank' href='/gemp-swccg-server/tournament/" + tournamentId + "/deck/" + player + "/html'>" + player + "</a>";
            else
                playerStr = player;

            standingsTable.append("<tr><td>" + currentStanding + "</td><td>" + playerStr + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td></tr>");
        }

        for (var k = secondColumnBaseIndex; k < standings.length; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            var playerStr;
            if (tournamentStage == "Finished")
                playerStr = "<a target='_blank' href='/gemp-swccg-server/tournament/" + tournamentId + "/deck/" + player + "/html'>" + player + "</a>";
            else
                playerStr = player;

            $("tr:eq(" + (k - secondColumnBaseIndex + 1) + ")", standingsTable).append("<td></td><td>" + currentStanding + "</td><td>" + playerStr + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td>");
        }

        return standingsTable;
    }
});
