package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;

/**
 * Defines a default implementation for league series data.
 */
public class DefaultLeagueSeriesData implements LeagueSeriesData {
    private LeaguePrizes _leaguePrizes;
    private boolean _limited;
    private String _name;
    private int _start;
    private int _end;
    private int _maxMatches;
    private String _format;
    private CollectionType _collectionType;

    /**
     * Creates league series data.
     * @param leaguePrizes the league prizes
     * @param limited true if limited, otherwise false
     * @param name the name of the league series
     * @param start the start date of the series as in integer
     * @param end the end date of the series as in integer
     * @param maxMatches the maximum number of matches a player can play
     * @param format the format of the league
     * @param collectionType the collection type used for the league
     */
    public DefaultLeagueSeriesData(LeaguePrizes leaguePrizes, boolean limited, String name, int start, int end, int maxMatches, String format, CollectionType collectionType) {
        _leaguePrizes = leaguePrizes;
        _limited = limited;
        _name = name;
        _start = start;
        _end = end;
        _maxMatches = maxMatches;
        _format = format;
        _collectionType = collectionType;
    }

    @Override
    public String getName() {
        return _name;
    }

    /**
     * Gets the start date of the league series as an integer.
     * @return start date as an integer
     */
    @Override
    public int getStart() {
        return _start;
    }

    /**
     * Gets the end date of the league series as an integer.
     * @return end date as an integer
     */
    @Override
    public int getEnd() {
        return _end;
    }

    /**
     * Gets the max number of matches a player can play in the series.
     * @return the max number of matches
     */
    @Override
    public int getMaxMatches() {
        return _maxMatches;
    }

    /**
     * Determines if the card collections used for the league is limited to the league.
     * @return true or false
     */
    @Override
    public boolean isLimited() {
        return _limited;
    }

    /**
     * Gets the format of the league.
     * @return the format
     */
    @Override
    public String getFormat() {
        return _format;
    }

    /**
     * Gets the collection type used for the league.
     * @return the collection type
     */
    @Override
    public CollectionType getCollectionType() {
        return _collectionType;
    }

    /**
     * Gets the prize for a league match winner.
     * @param winCountThisSeries the win count
     * @param totalGamesPlayedThisSeries the total games played
     * @return the prize, or null
     */
    @Override
    public CardCollection getPrizeForLeagueMatchWinner(int winCountThisSeries, int totalGamesPlayedThisSeries) {
        return _leaguePrizes.getPrizeForLeagueMatchWinner(winCountThisSeries, totalGamesPlayedThisSeries);
    }

    /**
     * Gets the prize for a league match loser.
     * @param winCountThisSeries the win count
     * @param totalGamesPlayedThisSeries the total games played
     * @return the prize, or null
     */
    @Override
    public CardCollection getPrizeForLeagueMatchLoser(int winCountThisSeries, int totalGamesPlayedThisSeries) {
        return _leaguePrizes.getPrizeForLeagueMatchLoser(winCountThisSeries, totalGamesPlayedThisSeries);
    }
}
