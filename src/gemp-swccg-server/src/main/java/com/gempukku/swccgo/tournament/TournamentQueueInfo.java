package com.gempukku.swccgo.tournament;

public class TournamentQueueInfo {
    private int _cost;
    private long _startTime;
    private String _tournamentName;
    private String _scheduledTournamentId;
    private String _format;
    private String _playOffSystem;
    private String _prizeScheme;
    private int _minimumPlayers;

    public TournamentQueueInfo(String scheduledTournamentId, String tournamentName, String format, long startTime, int cost, String playOffSystem, String prizeScheme, int minimumPlayers) {
        _scheduledTournamentId = scheduledTournamentId;
        _tournamentName = tournamentName;
        _format = format;
        _startTime = startTime;
        _cost = cost;
        _playOffSystem = playOffSystem;
        _prizeScheme = prizeScheme;
        _minimumPlayers = minimumPlayers;
    }

    public String getScheduledTournamentId() {
        return _scheduledTournamentId;
    }

    public int getCost() {
        return _cost;
    }

    public long getStartTime() {
        return _startTime;
    }

    public String getTournamentName() {
        return _tournamentName;
    }

    public String getFormat() {
        return _format;
    }

    public String getPlayOffSystem() {
        return _playOffSystem;
    }

    public String getPrizeScheme() {
        return _prizeScheme;
    }

    public int getMinimumPlayers() {
        return _minimumPlayers;
    }
}
