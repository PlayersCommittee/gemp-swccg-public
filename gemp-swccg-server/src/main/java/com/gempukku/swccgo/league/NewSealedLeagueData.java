package com.gempukku.swccgo.league;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Defines the data for a sealed league.
 */
public class NewSealedLeagueData implements LeagueData {
    private SealedLeagueType _leagueType;
    private List<LeagueSeriesData> _series;
    private CollectionType _collectionType;
    private CollectionType _prizeCollectionType = CollectionType.MY_CARDS;
    private LeaguePrizes _leaguePrizes;
    private SealedLeagueProduct _leagueProduct;

    /**
     * Creates the data for a sealed league.
     * @param parameters the input parameters
     */
    public NewSealedLeagueData(SwccgCardBlueprintLibrary library, String parameters) {
        _leaguePrizes = new FixedLeaguePrizes(library);

        String[] params = parameters.split(",");
        _leagueType = SealedLeagueType.getLeagueType(params[0]);
        int start = Integer.parseInt(params[1]);
        int seriesDuration = Integer.parseInt(params[2]);
        int maxMatches = Integer.parseInt(params[3]);

        _collectionType = new CollectionType(params[4], params[5]);
        _leagueProduct = new SealedLeagueProduct();
        int numSeriesInLeague = _leagueProduct.getNumSeriesInLeague(_leagueType.getSealedCode());
        _series = new LinkedList<LeagueSeriesData>();

        for (int i = 0; i < numSeriesInLeague; ++i) {
            _series.add(
                    new DefaultLeagueSeriesData(_leaguePrizes, true, "Series " + (i + 1),
                            DateUtils.offsetDate(start, i * seriesDuration), DateUtils.offsetDate(start, (i + 1) * seriesDuration - 1), maxMatches,
                            _leagueType.getFormatCode(), _collectionType));
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
        MutableCardCollection startingCollection = new DefaultCardCollection(excludePackDuplicates(_leagueType));

        for (int i = 0; i < _series.size(); ++i) {
            LeagueSeriesData series = _series.get(i);
            if (currentTime >= series.getStart()) {
                CardCollection leagueProduct = _leagueProduct.getCollectionForSeries(_leagueType.getSealedCode(), i);
                for (Map.Entry<String, CardCollection.Item> seriesCollectionItem : leagueProduct.getAll().entrySet()) {
                    startingCollection.addItem(seriesCollectionItem.getKey(), seriesCollectionItem.getValue().getCount());
                }
            }
        }
        collectionsManager.addPlayerCollection(true, "Sealed league product", player, _collectionType, startingCollection);
        return startingCollection;
    }

    private boolean excludePackDuplicates(SealedLeagueType sealedLeagueType) {
        //exclude duplicates from cube
        if(sealedLeagueType.getFormatCode().equals("cube"))
            return true;

        return false;
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

        for (int i = status; i < _series.size(); ++i) {
            LeagueSeriesData series = _series.get(i);
            if (currentTime >= series.getStart()) {
                Map<Player, CardCollection> map = collectionsManager.getPlayersCollection(_collectionType.getCode());
                for (Map.Entry<Player, CardCollection> playerCardCollectionEntry : map.entrySet()) {
                    CardCollection leagueProduct = _leagueProduct.getCollectionForSeries(_leagueType.getSealedCode(), i);
                    collectionsManager.addItemsToPlayerCollection(true, "New sealed league product", playerCardCollectionEntry.getKey(), _collectionType, leagueProduct.getAll().values());
                }
                status = i + 1;
            }
        }

        int maxGamesTotal = 0;
        for (LeagueSeriesData series : _series) {
            maxGamesTotal += series.getMaxMatches();
        }

        if (status == _series.size()) {
            LeagueSeriesData lastSeries = _series.get(_series.size() - 1);
            if (currentTime > DateUtils.offsetDate(lastSeries.getEnd(), 1)) {
                for (PlayerStanding leagueStanding : leagueStandings) {
                    CardCollection leaguePrize = _leaguePrizes.getPrizeForLeague(leagueStanding.getStanding(), leagueStandings.size(), leagueStanding.getGamesPlayed(), maxGamesTotal, _collectionType);
                    if (leaguePrize != null)
                        collectionsManager.addItemsToPlayerCollection(true, "End of league prizes", leagueStanding.getPlayerName(), _prizeCollectionType, leaguePrize.getAll().values());
                }
                status++;
            }
        }

        return status;
    }
}