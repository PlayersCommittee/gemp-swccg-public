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
 * Defines a Premiere starter set.
 */
public class PremiereStarterSet extends BasePackagedCardProduct {
    private Random _random = new Random();
    private List<String> _darkCommons = new ArrayList<String>();
    private List<String> _lightCommons = new ArrayList<String>();
    private List<String> _darkUncommons = new ArrayList<String>();
    private List<String> _lightUncommons = new ArrayList<String>();
    private List<String> _darkRares = new ArrayList<String>();
    private List<String> _lightRares = new ArrayList<String>();

    /**
     * Creates a Premiere starter set.
     * @param library the blueprint library
     */
    public PremiereStarterSet(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        SetRarity setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.PREMIERE.getSetNumber()));

        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.R1)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkRares.add(blueprintId);
                }
                else {
                    _lightRares.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.R2)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkRares.add(blueprintId);
                    _darkRares.add(blueprintId);
                }
                else {
                    _lightRares.add(blueprintId);
                    _lightRares.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.U1)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkUncommons.add(blueprintId);
                }
                else {
                    _lightUncommons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.U2)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkUncommons.add(blueprintId);
                    _darkUncommons.add(blueprintId);
                }
                else {
                    _lightUncommons.add(blueprintId);
                    _lightUncommons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.C1)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkCommons.add(blueprintId);
                }
                else {
                    _lightCommons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.C2)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkCommons.add(blueprintId);
                    _darkCommons.add(blueprintId);
                }
                else {
                    _lightCommons.add(blueprintId);
                    _lightCommons.add(blueprintId);
                }
            }
        }
        for (String blueprintId : setRarity.getCardsOfRarity(Rarity.C3)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.getSide() == Side.DARK) {
                    _darkCommons.add(blueprintId);
                    _darkCommons.add(blueprintId);
                    _darkCommons.add(blueprintId);
                }
                else {
                    _lightCommons.add(blueprintId);
                    _lightCommons.add(blueprintId);
                    _lightCommons.add(blueprintId);
                }
            }
        }
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.PREMIERE_STARTER_SET;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.PREMIERE_STARTER_SET;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCard(result, _darkUncommons, 7);
        addRandomCard(result, _lightUncommons, 7);
        addRandomCard(result, _darkRares, 1);
        addRandomCard(result, _lightRares, 1);
        addRandomCard(result, _darkCommons, 22);
        addRandomCard(result, _lightCommons, 22);
        return result;
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
