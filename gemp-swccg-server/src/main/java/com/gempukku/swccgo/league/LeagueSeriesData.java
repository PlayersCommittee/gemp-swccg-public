package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;

/**
 * Defines the methods that a class providing implementation of league series data must implement.
 */
public interface LeagueSeriesData {

    /**
     * Gets the name of the league.
     * @return the name
     */
    String getName();

    /**
     * Gets the start date of the league series as an integer.
     * @return start date as an integer
     */
    int getStart();

    /**
     * Gets the end date of the league series as an integer.
     * @return end date as an integer
     */
    int getEnd();

    /**
     * Gets the max number of matches a player can play in the series.
     * @return the max number of matches
     */
    int getMaxMatches();

    /**
     * Determines if the card collections used for the league is limited to the league.
     * @return true or false
     */
    boolean isLimited();

    /**
     * Gets the format of the league.
     * @return the format
     */
    String getFormat();

    /**
     * Gets the collection type used for the league.
     * @return the collection type
     */
    CollectionType getCollectionType();

    /**
     * Gets the prize for a league match winner.
     * @param winCountThisSeries the win count
     * @param totalGamesPlayedThisSeries the total games played
     * @return the prize, or null
     */
    CardCollection getPrizeForLeagueMatchWinner(int winCountThisSeries, int totalGamesPlayedThisSeries);

    /**
     * Gets the prize for a league match loser.
     * @param winCountThisSeries the win count
     * @param totalGamesPlayedThisSeries the total games played
     * @return the prize, or null
     */
    CardCollection getPrizeForLeagueMatchLoser(int winCountThisSeries, int totalGamesPlayedThisSeries);
}
