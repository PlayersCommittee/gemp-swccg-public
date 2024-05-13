package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Death Star II dark pre-constructed deck.
 */
public class DeathStarIIDarkPreConstructedDeck extends BasePackagedCardProduct {
    private List<String> _fixed = new ArrayList<String>();

    /**
     * Creates a Death Star II dark pre-constructed deck.
     * @param library the blueprint library
     */
    public DeathStarIIDarkPreConstructedDeck(SwccgCardBlueprintLibrary library) {
        super(library);

        _fixed.add("9_98");   // Admiral Piett
        _fixed.add("9_101");  // Captain Jonus
        _fixed.add("8_96");   // Corporal Drazin
        _fixed.add("9_107");  // DS-181-3
        _fixed.add("9_108");  // DS-181-4
        _fixed.add("8_100");  // Elite Squadron Stormtrooper
        _fixed.add("8_100");  // Elite Squadron Stormtrooper
        _fixed.add("8_100");  // Elite Squadron Stormtrooper
        _fixed.add("8_101");  // Lieutenant Arnet
        _fixed.add("8_102");  // Lieutenant Grond
        _fixed.add("9_112");  // Lieutenant Hebsly
        _fixed.add("2_103");  // Reserve Pilot
        _fixed.add("8_114");  // Sergeant Tarl
        _fixed.add("8_118");  // Battle Order
        _fixed.add("9_121");  // Combat Response
        _fixed.add("9_126");  // Inconsequential Losses
        _fixed.add("8_136");  // Combat Readiness
        _fixed.add("1_241");  // Dark Maneuvers
        _fixed.add("1_241");  // Dark Maneuvers
        _fixed.add("7_250");  // Flawless Marksmanship
        _fixed.add("2_132");  // Ghhhk
        _fixed.add("1_251");  // Imperial Reinforcements
        _fixed.add("2_135");  // Monnok
        _fixed.add("2_135");  // Monnok
        _fixed.add("9_139");  // Prepared Defenses
        _fixed.add("8_157");  // Endor
        _fixed.add("8_158");  // Endor: Ancient Forest
        _fixed.add("8_159");  // Endor: Back Door
        _fixed.add("8_165");  // Endor: Great Forest
        _fixed.add("8_166");  // Endor: Landing Platform (Docking Bay)
        _fixed.add("1_288");  // Kessel
        _fixed.add("9_149");  // Mon Calamari
        _fixed.add("9_150");  // Sullust
        _fixed.add("1_300");  // Black 3
        _fixed.add("9_164");  // Saber 3
        _fixed.add("9_165");  // Saber 4
        _fixed.add("9_167");  // Scimitar 2
        _fixed.add("9_170");  // Scythe 3
        _fixed.add("9_171");  // Scythe Squadron TIE
        _fixed.add("9_171");  // Scythe Squadron TIE
        _fixed.add("7_308");  // TIE Defender Mark I
        _fixed.add("7_308");  // TIE Defender Mark I
        _fixed.add("7_308");  // TIE Defender Mark I
        _fixed.add("9_175");  // TIE Interceptor
        _fixed.add("9_175");  // TIE Interceptor
        _fixed.add("9_175");  // TIE Interceptor
        _fixed.add("2_155");  // Victory-Class Star Destroyer
        _fixed.add("2_155");  // Victory-Class Star Destroyer
        _fixed.add("2_155");  // Victory-Class Star Destroyer
        _fixed.add("8_170");  // Tempest 1
        _fixed.add("8_171");  // Tempest Scout
        _fixed.add("8_171");  // Tempest Scout
        _fixed.add("8_174");  // Tempest Scout 3
        _fixed.add("1_312");  // Blaster Rifle
        _fixed.add("1_312");  // Blaster Rifle
        _fixed.add("9_177");  // Concussion Missiles
        _fixed.add("2_158");  // Enhanced TIE Laser Cannon
        _fixed.add("7_322");  // Intruder Missile
        _fixed.add("7_322");  // Intruder Missile
        _fixed.add("9_181");  // SFS L-s7.2 TIE Cannon

        filterNonExistingCards(_fixed);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_DARK_DECK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.DEATH_STAR_II_PRE_CONSTRUCTED_DECK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFixedCard(result, _fixed, 60);
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
}
