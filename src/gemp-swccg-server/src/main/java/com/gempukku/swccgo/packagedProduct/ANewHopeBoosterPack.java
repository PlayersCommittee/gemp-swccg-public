package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines an A New Hope booster pack.
 */
public class ANewHopeBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;

    /**
     * Creates an A New Hope booster pack.
     * @param library the blueprint library
     */
    public ANewHopeBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.A_NEW_HOPE.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.A_NEW_HOPE_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.A_NEW_HOPE_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomUncommonCard(result, 4);
        addRandomRareCard(result, 1);
        addRandomCommonCard(result, 10);
        return result;
    }

    /**
     * Adds random Rare cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomRareCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R1));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R2));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R2));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Uncommon cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomUncommonCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U1));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U2));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U2));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Common cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCommonCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C1));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C2));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C2));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C3));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C3));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C3));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
