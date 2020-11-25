package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Virtual Alternate Image booster pack.
 */
public class VirtualAlternateImageBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    /**
     * Creates a Virtual Alternate Image booster pack.
     * @param library the blueprint library
     */
    public VirtualAlternateImageBoosterPack(SwccgCardBlueprintLibrary library) {
        this(library, true, true);
    }

        /**
         * Creates a Virtual Alternate Image booster pack.
         * @param library the blueprint library
         * @param includeNonEpisodeI false if non-Episode I cards should be removed
         * @param includeDefensiveShields false if defensive shields and starting effects should be removed
         */
    public VirtualAlternateImageBoosterPack(SwccgCardBlueprintLibrary library, boolean includeNonEpisodeI, boolean includeDefensiveShields) {
        super(library);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCard(result, 4);
        return result;
    }

    /**
     * Adds random cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.add("204_29^");
        possibleCards.add("213_1^");
        possibleCards.add("213_2^");
        possibleCards.add("213_3^");
        possibleCards.add("213_4^");
        possibleCards.add("213_5^");
        possibleCards.add("213_6^");
        possibleCards.add("213_7^");
        possibleCards.add("213_8^");
        possibleCards.add("213_9^");
        possibleCards.add("213_10^");
        possibleCards.add("213_11^");

        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
