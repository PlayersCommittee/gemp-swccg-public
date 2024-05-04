package com.gempukku.swccgo.db;

import java.util.Date;

public class LeagueDecklistEntry {
    private String _leagueName;
    private Date _startTime;
    private String _player;
    private String _side;
    private String _deck;

    public LeagueDecklistEntry(String leagueName, Date startTime, String player, String side, String deck) {
        _leagueName = leagueName;
        _startTime = startTime;
        _player = player;
        _side = side;
        _deck = deck;
    }

    public String getLeagueName() {
        return _leagueName;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public String getPlayer() {
        return _player;
    }

    public String getSide() {
        return _side;
    }

    public String getDeck() {
        return _deck;
    }
}
