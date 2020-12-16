package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Watto's Cube booster pack.
 */
public class WattosCubeDraftPack extends BasePackagedCardProduct {
    private final int DRAFT_PACK_CARD_COUNT = 9;
    private Random _random = new Random();
    private String _side;

    /**
     * Creates a Watto's Cube booster pack.
     * @param library the blueprint library
     * @param side Dark or Light
     */
    public WattosCubeDraftPack(SwccgCardBlueprintLibrary library, String side) {
        super(library);
        _side = side;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _side.equals("Dark") ? ProductName.CUBE_DRAFT_PACK_DARK : ProductName.CUBE_DRAFT_PACK_LIGHT;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return 0.0f;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        return openPackageWithExclusions(Collections.<String>emptySet());
    }

    public List<CardCollection.Item> openPackageWithExclusions(Set<String> exclusions) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCardWithExclusions(result, DRAFT_PACK_CARD_COUNT, exclusions);
        return result;
    }

    private void addRandomCardWithExclusions(List<CardCollection.Item> result, int count,Set<String> exclusions) {
        List<String> possibleCards = new ArrayList<String>();
        switch(_side) {
            case "Dark":
                possibleCards.addAll(darkDraftCards());
                break;
            case "Light":
                possibleCards.addAll(lightDraftCards());
                break;
        }
        filterNonExistingCards(possibleCards);
        for(String s:exclusions) {
            possibleCards.remove(s);
        }
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }


    public List<String> darkDraftCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("1_1");

        return cards;
    }

    public List<String> lightDraftCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("1_1");

        return cards;
    }
}
