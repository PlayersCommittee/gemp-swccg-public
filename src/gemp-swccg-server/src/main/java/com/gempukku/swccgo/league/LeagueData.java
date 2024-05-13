package com.gempukku.swccgo.league;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;

import java.util.List;

/**
 * Defines the methods that implementation for league data must implement.
 */
public interface LeagueData {

    /**
     * Gets the league data for all series of the the league.
     * @return the league data
     */
    List<LeagueSeriesData> getSeries();

    /**
     * Adds the league product to the player's collection used for the league.
     * @param collectionsManager the collections manager
     * @param player the player
     * @param currentTime the current time
     * @return the league product
     */
    CardCollection joinLeague(CollectionsManager collectionsManager, Player player, int currentTime);

    /**
     * Adds the league product for the current series to the player's collection used for the league.
     * After the league is complete, adds any prizes to the player's collection.
     * @param collectionsManager the collections manager
     * @param leagueStandings the league standings
     * @param oldStatus the old status (number of series processed)
     * @param currentTime the current time
     * @return the new status (number of series processed)
     */
    int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime);

    boolean isSealed();
}
