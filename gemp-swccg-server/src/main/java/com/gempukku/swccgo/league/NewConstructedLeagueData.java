package com.gempukku.swccgo.league;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the data for a constructed league.
 */
public class NewConstructedLeagueData implements LeagueData {
    private LeaguePrizes _leaguePrizes;
    private List<LeagueSeriesData> _series = new ArrayList<LeagueSeriesData>();

    private CollectionType _prizeCollectionType = CollectionType.MY_CARDS;
    private CollectionType _collectionType;

    public NewConstructedLeagueData(SwccgCardBlueprintLibrary library, String parameters) {
        _leaguePrizes = new FixedLeaguePrizes(library);
        String[] params = parameters.split(",");
        int start = Integer.parseInt(params[0]);
        if ("default".equals(params[1]))
            _collectionType = CollectionType.ALL_CARDS;
        else if ("permanent".equals(params[1]))
            _collectionType = CollectionType.MY_CARDS;
        else
            throw new IllegalArgumentException("Unknown collection type");
        int series = Integer.parseInt(params[2]);

        int seriesStart = start;
        for (int i = 0; i < series; ++i) {
            String format = params[3 + i * 3];
            int duration = Integer.parseInt(params[4 + i * 3]);
            int maxMatches = Integer.parseInt(params[5 + i * 3]);
            _series.add(new DefaultLeagueSeriesData(_leaguePrizes, false, "Series " + (i + 1),
                    seriesStart, DateUtils.offsetDate(seriesStart, duration - 1),
                    maxMatches, format, _collectionType));

            seriesStart = DateUtils.offsetDate(seriesStart, duration);
        }
    }

    /**
     * Gets the league data for all series of the the league.
     * @return the league data
     */
    @Override
    public List<LeagueSeriesData> getSeries() {
        return Collections.unmodifiableList(_series);
    }

    /**
     * Adds the league product to the player's collection used for the league.
     * @param collectionsManager the collection manager
     * @param player the player
     * @param currentTime the current time
     * @return the league product
     */
    @Override
    public CardCollection joinLeague(CollectionsManager collectionsManager, Player player, int currentTime) {
        return null;
    }

    /**
     * Adds the league product for the current series to the player's collection used for the league.
     * After the league is complete, adds any prizes to the player's collection.
     * @param collectionsManager the collection manager
     * @param leagueStandings the league standings
     * @param oldStatus the old status (number of series processed)
     * @param currentTime the current time
     * @return the new status (number of series processed)
     */
    @Override
    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime) {
        int status = oldStatus;
        if (status == 0) {
            int maxGamesPlayed = 0;
            for (LeagueSeriesData series : _series) {
                maxGamesPlayed += series.getMaxMatches();
            }

            LeagueSeriesData lastSeries = _series.get(_series.size() - 1);
            if (currentTime > DateUtils.offsetDate(lastSeries.getEnd(), 1)) {
                for (PlayerStanding leagueStanding : leagueStandings) {
                    CardCollection leaguePrize = _leaguePrizes.getPrizeForLeague(leagueStanding.getStanding(), leagueStandings.size(), leagueStanding.getGamesPlayed(), maxGamesPlayed, _collectionType);
                    if (leaguePrize != null)
                        collectionsManager.addItemsToPlayerCollection(true, "End of league prizes", leagueStanding.getPlayerName(), _prizeCollectionType, leaguePrize.getAll().values());
                }
                status++;
            }
        }

        return status;
    }
}
