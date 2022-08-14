package com.gempukku.swccgo.league;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines the prizes used for leagues.
 */
public class FixedLeaguePrizes implements LeaguePrizes {
    private SwccgCardBlueprintLibrary _library;
    private List<String> _commonsAndFixed = new ArrayList<String>();
    private List<String> _uncommonsAndPreview = new ArrayList<String>();
    private List<String> _raresAndPremiums = new ArrayList<String>();

    /**
     * Creates the prizes used for leagues.
     * @param library the card blueprint library
     */
    public FixedLeaguePrizes(SwccgCardBlueprintLibrary library) {
        _library = library;
        RarityReader rarityReader = new RarityReader();
        for (int i = 1; i < (1 + CardCounts.FULL_SETS_CARD_COUNTS.length); i++) {
            addCardsToPrizePools(rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i < (101 + CardCounts.PREMIUM_SETS_CARD_COUNTS.length); i++) {
            addCardsToPrizePools(rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i < (200 + CardCounts.VIRTUAL_SETS_CARD_COUNTS.length); i++) {
            addCardsToPrizePools(rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 301; i < (301 + CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS.length); i++) {
            addCardsToPrizePools(rarityReader.getSetRarity(String.valueOf(i)));
        }
    }

    /**
     * Adds the cards to the prize pools.
     * @param setRarity the rarity data for a set
     */
    private void addCardsToPrizePools(SetRarity setRarity) {
        _commonsAndFixed.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.C)));
        _commonsAndFixed.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.C1)));
        _commonsAndFixed.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.C2)));
        _commonsAndFixed.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.C3)));
        _commonsAndFixed.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.F)));
        _uncommonsAndPreview.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.U)));
        _uncommonsAndPreview.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.U1)));
        _uncommonsAndPreview.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.U2)));
        _uncommonsAndPreview.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.F)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.PM)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.R)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.R1)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.R2)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.XR)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.UR)));
        _raresAndPremiums.addAll(filterNonExistingCards(setRarity.getCardsOfRarity(Rarity.V)));
    }

    /**
     * Removes any cards from the collection that do not have a blueprint id that matches an existing card in the blueprint library.
     * @param origCardList the cards (identified by blueprint id)
     */
    private Collection<String> filterNonExistingCards(Collection<String> origCardList) {
        List<String> cards = new ArrayList<String>(origCardList);
        Iterator<String> iterator = cards.iterator();
        while (iterator.hasNext()) {
            String blueprintId = iterator.next();
            if (blueprintId.contains("_")) {
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                if (blueprint == null) {
                    iterator.remove();
                }
            }
        }
        return cards;
    }

    /**
     * Gets the prize that the winner of a league match wins.
     * @param winCountThisSeries the number of wins the player has won so far in the current league series
     * @param totalGamesPlayedThisSeries the number of games the player has played so far in the current league series
     * @return the prize won, or null
     */
    @Override
    public CardCollection getPrizeForLeagueMatchWinner(int winCountThisSeries, int totalGamesPlayedThisSeries) {
        if (winCountThisSeries % 2 == 1) {
            DefaultCardCollection winnerPrize = new DefaultCardCollection();

            if (winCountThisSeries <= 4) {
                winnerPrize.addItem(getRandom(_commonsAndFixed) + "*", 1);
            }
            else if (winCountThisSeries <= 8) {
                winnerPrize.addItem(getRandom(_uncommonsAndPreview) + "*", 1);
            }
            else {
                winnerPrize.addItem(getRandom(_raresAndPremiums) + "*", 1);
            }

            return winnerPrize;
        }
        return null;
    }

    /**
     * Gets the prize that the loser of a league match wins.
     * @param winCountThisSeries the number of wins the player has won so far in the current league series
     * @param totalGamesPlayedThisSeries the number of games the player has played so far in the current league series
     * @return the prize won, or null
     */
    @Override
    public CardCollection getPrizeForLeagueMatchLoser(int winCountThisSeries, int totalGamesPlayedThisSeries) {
        return null;
    }

    /**
     * Gets the prize that the player wins at the end of the league.
     * @param position the position that the player ended at in the standings
     * @param playersCount the number of players in the league
     * @param gamesPlayed the number of games the player played in the league
     * @param maxGamesPlayed the max number of games that a player could play in the league
     * @param collectionType the collection type used for the league
     * @return the prize won, or null
     */
    @Override
    public CardCollection getPrizeForLeague(int position, int playersCount, int gamesPlayed, int maxGamesPlayed, CollectionType collectionType) {
        return getStandardPrizeForLeague(position, playersCount, gamesPlayed, maxGamesPlayed);
    }

    /**
     * Gets the prize for finishing a sealed league.
     * @param position the position that the player ended at in the standings
     * @param playersCount the number of players in the league
     * @param gamesPlayed the number of games the player played in the league
     * @param maxGamesPlayed the max number of games that a player could play in the league
     * @return the prize won, or null
     */
    private CardCollection getStandardPrizeForLeague(int position, int playersCount, int gamesPlayed, int maxGamesPlayed) {
        // Must have played at least 2 games
        if (gamesPlayed < 2) {
            return null;
        }

        // 1st - 5 reflections packs, 5 non-reflections packs
        // 2st - 3 reflections packs, 5 non-reflections packs
        // 3st - 1 reflections packs, 5 non-reflections packs
        // 4th-5th - 5 non-reflections packs
        // 6th-10th - 3 non-reflections packs
        // Others - 1 non-reflections pack
        DefaultCardCollection prize = new DefaultCardCollection();
        prize.addItem("(S)Booster Choice -- Reflections", getReflectionsPackCountForPrize(position));
        prize.addItem("(S)Booster Choice -- Non-Reflections", getNonReflectionsPackCountForPrize(position));
        if (!prize.getAll().isEmpty()) {
            return prize;
        }

        return null;
    }

    /**
     * Gets the number of Reflections (I, II, III) packs won by the player in final specified position in the league.
     * @param position the position
     * @return the number of packs
     */
    private int getReflectionsPackCountForPrize(int position) {
        if (position <= 1)
            return 5;
        if (position <= 2)
            return 3;
        if (position <= 3)
            return 1;
        return 0;
    }

    /**
     * Gets the number of non-Reflections (I, II, III) packs won by the player in final specified position in the league.
     * @param position the position
     * @return the number of packs
     */
    private int getNonReflectionsPackCountForPrize(int position) {
        if (position <= 5)
            return 5;
        if (position <= 10)
            return 3;
        return 1;
    }

    /**
     * Gets a random string from the list.
     * @param list the list
     * @return a string
     */
    private String getRandom(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    /**
     * Gets a specified number of random foils from the list.
     * @param list the list
     * @param count the count
     * @return the foils
     */
    private List<String> getRandomFoil(List<String> list, int count) {
        List<String> result = new LinkedList<String>();
        for (String element : list) {
            result.add(element + "*");
        }
        Collections.shuffle(result);
        return result.subList(0, count);
    }
}
