var GameHistoryUI = Class.extend({
    communication:null,
    itemStart:0,
    pageSize:20,

    init:function (url) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });
        this.loadHistory();
    },

    loadHistory:function () {
        var that = this;
        this.communication.getGameHistory(this.itemStart, this.pageSize,
            function (xml) {
                that.loadedGameHistory(xml);
            });
    },

    loadedGameHistory:function (xml) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'gameHistory') {
            var historyTable = $("<table class='gameHistory'></table>");
            historyTable.append("<tr><th>Format</th><th>Tournament</th><th>Deck</th><th>Winner</th><th>Loser</th><th>Win reason</th><th>Lose reason</th><th>Finished on</th><th>Replay link</th></tr>");

            var entries = root.getElementsByTagName("historyEntry");
            for (var i = 0; i < entries.length; i++) {
                var historyEntry = entries[i];
                var format = historyEntry.getAttribute("formatName");
                var tournament = historyEntry.getAttribute("tournament");
                var deck = historyEntry.getAttribute("deckName");
                var winner = historyEntry.getAttribute("winner");
                var loser = historyEntry.getAttribute("loser");
                var winReason = historyEntry.getAttribute("winReason");
                var loseReason = historyEntry.getAttribute("loseReason");
                var endTime = formatDate(new Date(parseInt(historyEntry.getAttribute("endTime"))));
                var gameRecordingId = historyEntry.getAttribute("gameRecordingId");

                var row = $("<tr></tr>");
                if (format != null)
                    row.append($("<td></td>").html(format));
                else
                    row.append($("<td></td>").html("&nbsp;"));
                if (tournament != null)
                    row.append($("<td></td>").html(tournament));
                else
                    row.append($("<td></td>").html("&nbsp;"));
                if (deck != null)
                    row.append($("<td></td>").html(deck));
                else
                    row.append($("<td></td>").html("&nbsp;"));
                row.append($("<td></td>").html(winner));
                row.append($("<td></td>").html(loser));
                row.append($("<td></td>").html(winReason));
                row.append($("<td></td>").html(loseReason));
                row.append($("<td></td>").html(endTime));
                if (gameRecordingId != null) {
                    var link = "game.html?replayId=" + root.getAttribute("playerId") + "$" + gameRecordingId;
                    var linkElem = $("<a>replay game</a>");
                    linkElem.attr("href", link);
                    row.append($("<td></td>").html(linkElem));
                } else {
                    row.append($("<td></td>").html("<i>not stored</i>"));
                }

                historyTable.append(row);
            }

            $("#gameHistory").append(historyTable);
        }
    }
});