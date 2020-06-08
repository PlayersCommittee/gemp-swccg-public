package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Special Edition light starter deck.
 */
public class SpecialEditionLightStarterDeck extends BasePackagedCardProduct {
    private Random _random = new Random();
    private List<String> _fixed = new ArrayList<String>();
    private List<String> _commons = new ArrayList<String>();
    private List<String> _uncommons = new ArrayList<String>();
    private List<String> _rares = new ArrayList<String>();

    /**
     * Creates a Special Edition light starter deck.
     * @param library the blueprint library
     */
    public SpecialEditionLightStarterDeck(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        SetRarity setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.SPECIAL_EDITION.getSetNumber()));

        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.R)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.LIGHT) {
                    _rares.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.U)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.LIGHT) {
                    _uncommons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.C)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.LIGHT) {
                    _commons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.F)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.LIGHT) {
                    _fixed.add(blueprintId);
                }
            }
        }

        filterNonExistingCards(_fixed);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.SPECIAL_EDITION_LIGHT_STARTER_DECK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.SPECIAL_EDITION_STARTER_DECK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFixedCard(result, _fixed, 22);
        addRandomCard(result, _commons, 22);
        addRandomCard(result, _uncommons, 14);
        addRandomCard(result, _rares, 2);
        return result;
    }

    /**
     * Adds fixed cards to the list.
     * @param result the list of cards in the pack
     * @param fromCards the list of cards to add from
     * @param count the number cards to add
     */
    private void addFixedCard(List<CardCollection.Item> result, List<String> fromCards, int count) {
        addCards(result, fromCards.subList(0, Math.min(fromCards.size(), count)), false);
    }

    /**
     * Adds random cards to the list.
     * @param result the list of cards in the pack
     * @param fromCards the list of cards to add from
     * @param count the number cards to add
     */
    private void addRandomCard(List<CardCollection.Item> result, List<String> fromCards, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(fromCards);
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
