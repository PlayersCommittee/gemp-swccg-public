package com.gempukku.swccgo.competitive;

public class PlayerStanding {
    private String _playerName;
    private int _points;
    private int _gamesPlayed;
    private float _opponentWin;
    private int _standing;

    public PlayerStanding(String playerName, int points, int gamesPlayed) {
        _playerName = playerName;
        _points = points;
        _gamesPlayed = gamesPlayed;
    }

    public int getGamesPlayed() {
        return _gamesPlayed;
    }

    public float getOpponentWin() {
        return _opponentWin;
    }

    public String getPlayerName() {
        return _playerName;
    }

    public int getPoints() {
        return _points;
    }

    public int getStanding() {
        return _standing;
    }

    public void setOpponentWin(float opponentWin) {
        _opponentWin = opponentWin;
    }

    public void setStanding(int standing) {
        _standing = standing;
    }
}
