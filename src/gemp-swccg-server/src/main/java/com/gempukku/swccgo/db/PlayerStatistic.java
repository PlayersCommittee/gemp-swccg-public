package com.gempukku.swccgo.db;

public class PlayerStatistic {
    private String _deckName;
    private String _formatName;
    private int _wins;
    private int _losses;

    public PlayerStatistic(String deckName, String formatName, int wins, int losses) {
        _deckName = deckName;
        _formatName = formatName;
        _wins = wins;
        _losses = losses;
    }

    public String getDeckName() {
        return _deckName;
    }

    public String getFormatName() {
        return _formatName;
    }

    public int getWins() {
        return _wins;
    }

    public int getLosses() {
        return _losses;
    }
}
