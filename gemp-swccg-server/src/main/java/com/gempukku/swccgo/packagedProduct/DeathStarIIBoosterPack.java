package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Death Star II booster pack.
 */
public class DeathStarIIBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;

    /**
     * Creates a Death Star II booster pack.
     * @param library the blueprint library
     */
    public DeathStarIIBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.DEATH_STAR_II.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.DEATH_STAR_II_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.DEATH_STAR_II_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCommonCard(result, 7);
        addRandomRareCard(result, 1);
        addRandomUncommonCard(result, 3);
        return result;
    }

    /**
     * Adds random Rare cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomRareCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.UR));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R));
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
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U));
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
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
