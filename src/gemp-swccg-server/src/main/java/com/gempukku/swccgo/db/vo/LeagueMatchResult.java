package com.gempukku.swccgo.db.vo;

import com.gempukku.swccgo.competitive.CompetitiveMatchResult;

public class LeagueMatchResult implements CompetitiveMatchResult {
    private String _winner;
    private String _loser;
    private String _winnerSide;
    private String _loserSide;
    private String _serieName;

    public LeagueMatchResult(String serieName, String winner, String loser, String winnerSide, String loserSide) {
        _serieName = serieName;
        _winner = winner;
        _loser = loser;
        _winnerSide = winnerSide;
        _loserSide = loserSide;
    }

    public String getSerieName() {
        return _serieName;
    }

    public String getLoser() {
        return _loser;
    }

    public String getWinner() {
        return _winner;
    }

    public String getLoserSide() {
        if (_loserSide==null)
            return "Unknown";
        else
            return _loserSide;
    }

    public String getWinnerSide() {
        if (_winnerSide==null)
            return "Unknown";
        else
            return _winnerSide;
    }
}
