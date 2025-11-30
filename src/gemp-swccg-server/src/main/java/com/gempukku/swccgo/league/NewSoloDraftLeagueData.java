package com.gempukku.swccgo.league;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.*;

import javax.smartcardio.Card;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSoloDraftLeagueData implements LeagueData {
    public static final int HIGH_ENOUGH_PRIME_NUMBER = 8963;
    private SoloDraft _draft;
    private CollectionType _collectionType;
    private CollectionType _prizeCollectionType = CollectionType.MY_CARDS;
    private LeaguePrizes _leaguePrizes;
    private LeagueSeriesData _serie;

    public NewSoloDraftLeagueData(SwccgCardBlueprintLibrary library, SoloDraftDefinitions soloDraftDefinitions, String parameters) {
        _leaguePrizes = new FixedLeaguePrizes(library);

        String[] params = parameters.split(",");
        _draft = soloDraftDefinitions.getSoloDraft(params[0]);
        int start = Integer.parseInt(params[1]);
        int serieDuration = Integer.parseInt(params[2]);
        int maxMatches = Integer.parseInt(params[3]);

        _collectionType = new CollectionType(params[4], params[5]);

        _serie = new DefaultLeagueSeriesData(_leaguePrizes, true, "Serie 1",
                DateUtils.offsetDate(start, 0), DateUtils.offsetDate(start, serieDuration - 1), maxMatches,
                _draft.getFormat(), _collectionType);
    }

    public CollectionType getCollectionType() {
        return _collectionType;
    }

    @Override
    public SoloDraft getSoloDraft() {
        return _draft;
    }

    @Override
    public boolean isSoloDraftLeague() {
        return true;
    }

    @Override
    public boolean isSealed() { return false; }

    @Override
    public List<LeagueSeriesData> getSeries() {
        return Collections.singletonList(_serie);
    }

    private long getSeed(Player player) {
        return _collectionType.getCode().hashCode() + player.getId() * HIGH_ENOUGH_PRIME_NUMBER;
    }

    @Override
    public CardCollection joinLeague(CollectionsManager collectionsManager, Player player, int currentTime) {
        MutableCardCollection startingCollection = new DefaultCardCollection();
        long seed = getSeed(player);

        CardCollection leagueProduct = _draft.initializeNewCollection(seed);

        for (CardCollection.Item serieCollectionItem : leagueProduct.getAll().values())
            startingCollection.addItem(serieCollectionItem.getBlueprintId(), serieCollectionItem.getCount());

        startingCollection.setExtraInformation(createExtraInformation(seed));
        collectionsManager.addPlayerCollection(false, "Sealed league product", player, _collectionType, startingCollection);
        return startingCollection;
    }

    public void repairExtraInformation(CardCollection collection, Player player) {
        // we don't serialize to this to our db the way lotr does so we must recalculate it when empty
        // this being empty means the player in rejoining a draft that they started in a prior server reboot or cache state
        if (collection.getExtraInformation().size() == 0) {
            Map<String, Object> extraInformation = new HashMap<String, Object>();
            long seed = getSeed(player);
            int stage = _draft.currentStage(collection);
            stage -= _draft.fixedCardCount();
            extraInformation.put("finished", (_draft.hasNextStage(seed, stage) == false));
            extraInformation.put("stage", stage);
            extraInformation.put("stageCount", _draft.stageCount());
            extraInformation.put("seed", seed);
            collection.setExtraInformation(extraInformation);
        }
    }

    private Map<String, Object> createExtraInformation(long seed) {
        Map<String, Object> extraInformation = new HashMap<String, Object>();
        extraInformation.put("finished", false);
        extraInformation.put("stage", 0);
        extraInformation.put("stageCount", _draft.stageCount());
        extraInformation.put("seed", seed);
        return extraInformation;
    }

    @Override
    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime) {
        int status = oldStatus;

        if (status == 0) {
            if (currentTime > DateUtils.offsetDate(_serie.getEnd(), 1)) {
                int maxGamesTotal = _serie.getMaxMatches();

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
