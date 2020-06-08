package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines an Endor booster pack.
 */
public class EndorBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;
    private boolean _doNotReplaceRaresWithFoils;

    /**
     * Creates an Endor booster pack.
     * @param library the blueprint library
     * @param doNotReplaceRaresWithFoils true if rare in pack is not to be randomly replaced with a foi
     */
    public EndorBoosterPack(SwccgCardBlueprintLibrary library, boolean doNotReplaceRaresWithFoils) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.ENDOR.getSetNumber()));
        _doNotReplaceRaresWithFoils = doNotReplaceRaresWithFoils;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _doNotReplaceRaresWithFoils ? ProductName.ENDOR_BOOSTER_PACK_NO_RANDOM_FOIL : ProductName.ENDOR_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.ENDOR_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCommonCard(result, 5);
        addRandomUncommonCard(result, 3);
        addRandomRareOrFoilCard(result, 1);
        return result;
    }

    /**
     * Adds random Rare (or Foil) cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomRareOrFoilCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        // Approximately 1 of 9 times add a foil instead of a Rare
        if (!_doNotReplaceRaresWithFoils && (Math.random() * 9) >= 8) {
            // Common foils
            for (int i=0; i<9; ++i) {
                possibleCards.add("8_92");  // Biker Scout Trooper
                possibleCards.add("8_100"); // Elite Squadron Stormtrooper
                possibleCards.add("8_166"); // Endor: Landing Platform (Docking Bay) (Dark Side)
                possibleCards.add("8_169"); // Speeder Bike
                possibleCards.add("8_73");  // Endor: Ewok Village
                possibleCards.add("8_47");  // Ewok and Roll
                possibleCards.add("8_82");  // Ewok Glider
                possibleCards.add("8_24");  // Paploo
            }
            // Uncommon foils
            for (int i=0; i<4; ++i) {
                possibleCards.add("8_122"); // Early Warning Network
                possibleCards.add("8_146"); // Hot Pursuit
                possibleCards.add("8_149"); // Main Course
                possibleCards.add("8_2");   // Chewbacca of Kashyyyk
                possibleCards.add("8_8");   // Daughter of Skywalker
                possibleCards.add("8_46");  // Biker Scout Trooper
            }
            // Rare foils
            for (int i=0; i<2; ++i) {
                possibleCards.add("8_170"); // Tempest 1
                possibleCards.add("8_175"); // Tempest Scout 4
                possibleCards.add("8_15");  // General Solo
                possibleCards.add("8_31");  // Threepio
            }
            filterNonExistingCards(possibleCards);
            Collections.shuffle(possibleCards, _random);
            addCards(result, possibleCards.subList(0, count), true);
        }
        else {
            possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R));
            filterNonExistingCards(possibleCards);
            Collections.shuffle(possibleCards, _random);
            addCards(result, possibleCards.subList(0, count), false);
        }
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
