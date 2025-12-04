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

    loadedLeague:function (xml, leagueExtraInfoCssId) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'league') {
            $(leagueExtraInfoCssId).html("");

            var league = root;

            var leagueName = league.getAttribute("name");
            var leagueType = league.getAttribute("type");
            var cost = parseInt(league.getAttribute("cost"));
            var start = league.getAttribute("start");
            var end = league.getAttribute("end");
            var member = league.getAttribute("member");
            var joinable = league.getAttribute("joinable");
            var isSoloDraft = league.getAttribute("isSoloDraft");
            var draftable = league.getAttribute("draftable");
            var invitationOnly = league.getAttribute("invitationOnly");
            var registrationInfo = league.getAttribute("registrationInfo");

            $(leagueExtraInfoCssId).append("<div class='leagueName'>" + leagueName + "</div>");
            $(leagueExtraInfoCssId).append("<div class='leagueID'>League ID: " + leagueType + "</div>");

            if (invitationOnly == "true") {
                if (registrationInfo != "" && registrationInfo != "null")
                    $(leagueExtraInfoCssId).append("<div>Registration info: "+registrationInfo);
                else
                    $(leagueExtraInfoCssId).append("<div>Registration for this league by invitation only.</div>");

            } else {
                var costStr = formatPrice(cost);
                $(leagueExtraInfoCssId).append("<div class='leagueCost'><b>Cost:</b> " + costStr + "</div>");
            };

            if (member == "true") {
                var memberDiv = $("<div class='leagueMembership'>You are already a member of this league. </div>");
                if (draftable == "true") {
                    var draftBut = $("<button>--> Go to draft <--</button>").button();
                    var draftFunc = (function (leagueCode) {
                        return function() {
                            location.href = "/gemp-swccg/soloDraft.html?leagueType="+leagueCode;
                        };
                    })(leagueType);
                    draftBut.click(draftFunc);
                    memberDiv.append(draftBut);
                } else if (isSoloDraft == "true") {
                    // Solo draft league but not yet draftable (hasn't started yet)
                    // Show grayed out button with availability date
                    var grayedButton = $("<button disabled='disabled' class='draft-not-available'>Draft Available " + getDateString(start) + "</button>");
                    memberDiv.append(grayedButton);
                }
                $(leagueExtraInfoCssId).append(memberDiv);
            }
            else if (joinable == "true" && invitationOnly != "true") {
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
                $(leagueExtraInfoCssId).append(joinDiv);
            } else if (joinable == "true" && invitationOnly == "true") {
                var joinDiv = $("<div class='leagueMembership'>You're not a member of this league. </div>");
                $(leagueExtraInfoCssId).append(joinDiv);
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
                $(leagueExtraInfoCssId).append("<div class='serieName'>" + serieText + "</div>");

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
                $(leagueExtraInfoCssId).append(formatDiv);
                $(leagueExtraInfoCssId).append("<div><b>Collection:</b> " + collection + "</div>");

                tabContent.append("<div>Maximum ranked matches in serie: " + maxMatches + "</div>");

                var standings = serie.getElementsByTagName("standing");
                if (standings.length > 0)
                    tabContent.append(this.createStandingsTable(standings));
                tabDiv.append(tabContent);

                tabNavigation.append("<li><a href='#leagueserie" + j + "'>Serie " + (j + 1) + "</a></li>");
            }

            tabDiv.tabs();

            $(leagueExtraInfoCssId).append(tabDiv);
        }
    },

    loadedLeagueResults:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'leagues') {
            $("#myLeagueResults").html("");
            $("#leagueResults").html("");
            var myLeaguesCount = 0;

            var leagues = root.getElementsByTagName("league");
            for (var i = 0; i < leagues.length; i++) {
                var league = leagues[i];
                console.log(league);
                var leagueName = league.getAttribute("name");
                var leagueMember = league.getAttribute("member");
                var leagueType = league.getAttribute("type");
                var start = league.getAttribute("start");
                var end = league.getAttribute("end");
                var leagueExtraInfoCssId = "league-"+i+"-extra-info";

                if (leagueMember == "true") {
                    myLeaguesCount = myLeaguesCount + 1;

                    /* ALL Leagues */
                    $("#myLeagueResults").append("<div id='my-league-"+i+"-name' class='leagueName'>" + leagueName + "</div>");

                    var duration = '<span class="leagueDurationStart">' + getDateString(start) + '</span> to <span class="leagueDurationStop">' + getDateString(end) + '</span>';
                    $("#myLeagueResults").append("<div id='my-league-"+i+"-duration' class='leagueDuration'><b>Duration (GMT+0):</b> " + duration + "</div>");

                    var myDetailsBut = $('<button id="my-league-'+i+'-see-details-button" class="leagueSeeDetails">Display league details</button>').button();
                    $("#myLeagueResults").append(myDetailsBut);
                    $("#myLeagueResults").append("<div id='my-"+leagueExtraInfoCssId+"' class='leagueExtraInfo' style='display:none;'></div>");
                    $("#myLeagueResults").append("<hr class='leagueHr' />");

                    myDetailsBut.click(
                        (function (type, cssid, t) {
                            return function () {
                                that.communication.getLeague(type, function (xml) {
                                    that.loadedLeague(xml, cssid);
                                    $(cssid).slideToggle();
                                    console.log("Showing MY details for: "+cssid);
                                });
                            };
                        })(leagueType, "#my-"+leagueExtraInfoCssId, "")); // myDetailsBut.click

                } else {

                    /* ALL Leagues */
                    $("#leagueResults").append("<div id='league-"+i+"-name' class='leagueName'>" + leagueName + "</div>");

                    var duration = '<span class="leagueDurationStart">' + getDateString(start) + '</span> to <span class="leagueDurationStop">' + getDateString(end) + '</span>';
                    $("#leagueResults").append("<div id='league-"+i+"-duration' class='leagueDuration'><b>Duration (GMT+0):</b> " + duration + "</div>");

                    var detailsBut = $('<button id="league-'+i+'-see-details-button" class="leagueSeeDetails">Display league details</button>').button();
                    $("#leagueResults").append(detailsBut);
                    $("#leagueResults").append("<div id='"+leagueExtraInfoCssId+"' class='leagueExtraInfo' style='display:none;'></div>");
                    $("#leagueResults").append("<hr class='leagueHr' />");

                    detailsBut.click(
                        (function (type, cssid) {
                            return function () {
                                that.communication.getLeague(type, function (xml) {
                                    that.loadedLeague(xml, cssid);
                                    $(cssid).slideToggle();
                                    console.log("Showing ALL details for: "+cssid);
                                });
                            };
                        })(leagueType, "#"+leagueExtraInfoCssId)); // detailsBut.click

                } // leagueMember

            } // for
            if (myLeaguesCount == 0) {
                $("#myLeagueResults").html("You are not currently part of any leagues. Join one by expanding the league info in the <strong>All Leagues</strong> section and joining a league.");
            }
        } // root.tagName leagues
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

        standingsTable.append("<tr><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>W</th><th>L</th><th>Opp. Win %</th><th></th><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>W</th><th>L</th><th>Opp. Win %</th></tr>");

        var secondColumnBaseIndex = Math.ceil(standings.length / 2);

        for (var k = 0; k < secondColumnBaseIndex; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            standingsTable.append("<tr><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + ((points-gamesPlayed)/2) + "</td><td>" + (gamesPlayed-(points-gamesPlayed)/2) + "</td><td>" + opponentWinPerc + "</td></tr>");
        }

        for (var k = secondColumnBaseIndex; k < standings.length; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            $("tr:eq(" + (k - secondColumnBaseIndex + 1) + ")", standingsTable).append("<td></td><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + ((points-gamesPlayed)/2) + "</td><td>" + (gamesPlayed-(points-gamesPlayed)/2) + "</td><td>" + opponentWinPerc + "</td>");
        }

        return standingsTable;
    }
});
