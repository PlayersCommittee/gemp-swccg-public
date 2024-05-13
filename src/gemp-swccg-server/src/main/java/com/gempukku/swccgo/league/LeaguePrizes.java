package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;

/**
 * Defines the methods that a class that implements league prizes must implement.
 */
public interface LeaguePrizes {

    /**
     * Gets the prize that the winner of a league match wins.
     * @param winCountThisSeries the number of wins the player has won so far in the current league series
     * @param totalGamesPlayedThisSeries the number of games the player has played so far in the current league series
     * @return the prize won, or null
     */
    CardCollection getPrizeForLeagueMatchWinner(int winCountThisSeries, int totalGamesPlayedThisSeries);

    /**
     * Gets the prize that the loser of a league match wins.
     * @param winCountThisSeries the number of wins the player has won so far in the current league series
     * @param totalGamesPlayedThisSeries the number of games the player has played so far in the current league series
     * @return the prize won, or null
     */
    CardCollection getPrizeForLeagueMatchLoser(int winCountThisSeries, int totalGamesPlayedThisSeries);

    /**
     * Gets the prize that the player wins at the end of the league.
     * @param position the position that the player ended at in the standings
     * @param playersCount the number of players in the league
     * @param gamesPlayed the number of games the player played in the league
     * @param maxGamesPlayed the max number of games that a player could play in the league
     * @param collectionType the collection type used for the league
     * @return the prize won, or null
     */
    CardCollection getPrizeForLeague(int position, int playersCount, int gamesPlayed, int maxGamesPlayed, CollectionType collectionType);
}
