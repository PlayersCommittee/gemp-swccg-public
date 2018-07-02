var LeagueResultsUI = Class.extend({
    communication:null,
    questionDialog:null,
    formatDialog:null,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.questionDialog = $("<div></div>")
            .dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:false,
                modal:true,
                title:"League operation"
            });

        this.formatDialog = $("<div></div>")
            .dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:false,
                modal:true,
                title:"Format description"
            });

        this.loadResults();
    },

    loadResults:function () {
        var that = this;
        this.communication.getLeagues(
            function (xml) {
                that.loadedLeagueResults(xml);
            });
    },

    loadedLeague:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'league') {
            $("#leagueExtraInfo").html("");

            var league = root;

            var leagueName = league.getAttribute("name");
            var leagueType = league.getAttribute("type");
            var cost = parseInt(league.getAttribute("cost"));
            var start = league.getAttribute("start");
            var end = league.getAttribute("end");
            var member = league.getAttribute("member");
            var joinable = league.getAttribute("joinable");

            $("#leagueExtraInfo").append("<div class='leagueName'>" + leagueName + "</div>");
            $("#leagueExtraInfo").append("<div class='leagueID'>League ID: " + leagueType + "</div>");

            var costStr = formatPrice(cost);
            $("#leagueExtraInfo").append("<div class='leagueCost'><b>Cost:</b> " + costStr + "</div>");

            if (member == "true")
                $("#leagueExtraInfo").append("<div class='leagueMembership'>You are already a member of this league.</div>");
            else if (joinable == "true") {
                var joinBut = $("<button>Join league</button>").button();

                var joinFunc = (function (leagueCode, costString) {
                    return function () {
                        that.displayBuyAction("Do you want to join the league by paying " + costString + "?",
                            function () {
                                that.communication.joinLeague(leagueCode, function () {
                                    that.loadResults();
                                }, {
                                    "409":function () {
                                        alert("You don't have enough funds to join this league.");
                                    }
                                });
                            });
                    };
                })(leagueType, costStr);

                joinBut.click(joinFunc);
                var joinDiv = $("<div class='leagueMembership'>You're not a member of this league. </div>");
                joinDiv.append(joinBut);
                $("#leagueExtraInfo").append(joinDiv);
            }

            var tabDiv = $("<div width='100%'></div>");
            var tabNavigation = $("<ul></ul>");
            tabDiv.append(tabNavigation);

            // Overall tab
            var tabContent = $("<div id='leagueoverall'></div>");

            var standings = league.getElementsByTagName("leagueStanding");
            if (standings.length > 0)
                tabContent.append(this.createStandingsTable(standings));
            tabDiv.append(tabContent);

            tabNavigation.append("<li><a href='#leagueoverall'>Overall results</a></li>");
            tabNavigation.append("<li><a href='#leaguematches'>Your league matches</a></li>");

            var matchResults = $("<div id='leaguematches'></div>");
            tabDiv.append(matchResults);

            var series = league.getElementsByTagName("serie");
            for (var j = 0; j < series.length; j++) {
                var serie = series[j];
                matchResults.append("<div>Serie " + (j + 1) + "</div>");
                var matchGroup = $("<table class='standings'><tr><th>Winner</th><th>Loser</th></tr></table>");
                var matches = serie.getElementsByTagName("match");
                for (var k = 0; k<matches.length; k++) {
                    var match = matches[k];
                    matchGroup.append("<tr><td>"+match.getAttribute("winner")+"</td><td>"+match.getAttribute("loser")+"</td></tr>");
                }

                matchResults.append(matchGroup);

                var tabContent = $("<div id='leagueserie" + j + "'></div>");

                var serieName = serie.getAttribute("type");
                var serieStart = serie.getAttribute("start");
                var serieEnd = serie.getAttribute("end");
                var maxMatches = serie.getAttribute("maxMatches");
                var formatType = serie.getAttribute("formatType");
                var format = serie.getAttribute("format");
                var collection = serie.getAttribute("collection");
                var limited = serie.getAttribute("limited");

                var serieText = serieName + " - " + getDateString(serieStart) + " to " + getDateString(serieEnd);
                $("#leagueExtraInfo").append("<div class='serieName'>" + serieText + "</div>");

                var formatName = $("<span class='clickableFormat'>" + ((limited == "true") ? "" : "Constructed ") + format + "</span>");
                var formatDiv = $("<div><b>Format:</b> </div>");
                formatDiv.append(formatName);
                formatName.click(
                    (function (ft) {
                        return function () {
                            that.formatDialog.html("");
                            that.formatDialog.dialog("open");
                            that.communication.getFormat(ft,
                                function (html) {
                                    that.formatDialog.html(html);
                                });
                        };
                    })(formatType));
                $("#leagueExtraInfo").append(formatDiv);
                $("#leagueExtraInfo").append("<div><b>Collection:</b> " + collection + "</div>");

                tabContent.append("<div>Maximum ranked matches in serie: " + maxMatches + "</div>");

                var standings = serie.getElementsByTagName("standing");
                if (standings.length > 0)
                    tabContent.append(this.createStandingsTable(standings));
                tabDiv.append(tabContent);

                tabNavigation.append("<li><a href='#leagueserie" + j + "'>Serie " + (j + 1) + "</a></li>");
            }

            tabDiv.tabs();

            $("#leagueExtraInfo").append(tabDiv);
        }
    },

    loadedLeagueResults:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'leagues') {
            $("#leagueResults").html("");

            var leagues = root.getElementsByTagName("league");
            for (var i = 0; i < leagues.length; i++) {
                var league = leagues[i];
                var leagueName = league.getAttribute("name");
                var leagueType = league.getAttribute("type");
                var start = league.getAttribute("start");
                var end = league.getAttribute("end");

                $("#leagueResults").append("<div class='leagueName'>" + leagueName + "</div>");

                var duration = getDateString(start) + " to " + getDateString(end);
                $("#leagueResults").append("<div class='leagueDuration'><b>Duration (GMT+0):</b> " + duration + "</div>");

                var detailsBut = $("<button>See details</button>").button();
                detailsBut.click(
                    (function (type) {
                        return function () {
                            that.communication.getLeague(type,
                                function (xml) {
                                    that.loadedLeague(xml);
                                });
                        };
                    })(leagueType));
                $("#leagueResults").append(detailsBut);
            }

            $("#leagueResults").append("<hr />");
            $("#leagueResults").append("<div id='leagueExtraInfo'></div>");
        }
    },

    displayBuyAction:function (text, yesFunc) {
        var that = this;
        this.questionDialog.html("");
        this.questionDialog.html("<div style='scroll: auto'></div>");
        var questionDiv = $("<div>" + text + "</div>");
        questionDiv.append("<br/>");
        questionDiv.append($("<button>Yes</button>").button().click(
            function () {
                that.questionDialog.dialog("close");
                yesFunc();
            }));
        questionDiv.append($("<button>No</button>").button().click(
            function () {
                that.questionDialog.dialog("close");
            }));
        this.questionDialog.append(questionDiv);

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = 230;
        var vertSpace = 100;

        this.questionDialog.dialog({width:Math.min(horSpace, windowWidth), height:Math.min(vertSpace, windowHeight)});
        this.questionDialog.dialog("open");
    },

    createStandingsTable:function (standings) {
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

            standingsTable.append("<tr><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td></tr>");
        }

        for (var k = secondColumnBaseIndex; k < standings.length; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            $("tr:eq(" + (k - secondColumnBaseIndex + 1) + ")", standingsTable).append("<td></td><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + opponentWinPerc + "</td>");
        }

        return standingsTable;
    }
});