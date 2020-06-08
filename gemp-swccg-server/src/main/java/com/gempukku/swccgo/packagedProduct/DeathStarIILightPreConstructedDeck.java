package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Death Star II light pre-constructed deck.
 */
public class DeathStarIILightPreConstructedDeck extends BasePackagedCardProduct {
    private List<String> _fixed = new ArrayList<String>();

    /**
     * Creates a Death Star II light pre-constructed deck.
     * @param library the blueprint library
     */
    public DeathStarIILightPreConstructedDeck(SwccgCardBlueprintLibrary library) {
        super(library);

        _fixed.add("9_6");   // Admiral Ackbar
        _fixed.add("8_1");   // Captain Yutani
        _fixed.add("8_2");   // Chewbacca Of Kashyyyk
        _fixed.add("8_4");   // Corporal Beezer
        _fixed.add("8_5");   // Corporal Delevar
        _fixed.add("8_6");   // Corporal Janse
        _fixed.add("9_11");  // Corporal Midge
        _fixed.add("3_5");   // Derek 'Hobbie' Klivian
        _fixed.add("8_9");   // Dresselian Commando
        _fixed.add("8_9");   // Dresselian Commando
        _fixed.add("8_15");  // General Solo
        _fixed.add("9_15");  // Gray Squadron Y-wing Pilot
        _fixed.add("9_15");  // Gray Squadron Y-wing Pilot
        _fixed.add("9_18");  // Karie Neth
        _fixed.add("9_19");  // Keir Santage
        _fixed.add("9_20");  // Kin Kian
        _fixed.add("8_29");  // Sergeant Junkin
        _fixed.add("8_35");  // Battle Plan
        _fixed.add("9_39");  // Squadron Assignments
        _fixed.add("9_43");  // Superficial Damage
        _fixed.add("1_70");  // A Few Maneuvers
        _fixed.add("1_70");  // A Few Maneuvers
        _fixed.add("8_44");  // Careful Planning
        _fixed.add("2_49");  // Grimtaash
        _fixed.add("2_49");  // Grimtaash
        _fixed.add("9_51");  // Heading For The Medical Frigate
        _fixed.add("2_50");  // Houjix
        _fixed.add("7_105"); // Steady Aim
        _fixed.add("8_61");  // Take The Initiative
        _fixed.add("5_76");  // Bespin
        _fixed.add("8_68");  // Endor
        _fixed.add("8_69");  // Endor: Back Door
        _fixed.add("8_74");  // Endor: Great Forest
        _fixed.add("8_75");  // Endor: Hidden Forest Trail
        _fixed.add("8_76");  // Endor: Landing Platform (Docking Bay)
        _fixed.add("9_60");  // Sullust
        _fixed.add("1_127"); // Tatooine
        _fixed.add("9_62");  // A-wing
        _fixed.add("9_62");  // A-wing
        _fixed.add("9_62");  // A-wing
        _fixed.add("9_66");  // B-wing Bomber
        _fixed.add("9_66");  // B-wing Bomber
        _fixed.add("9_66");  // B-wing Bomber
        _fixed.add("9_69");  // Gray Squadron 1
        _fixed.add("9_70");  // Gray Squadron 2
        _fixed.add("9_80");  // Nebulon-B Frigate
        _fixed.add("9_80");  // Nebulon-B Frigate
        _fixed.add("9_80");  // Nebulon-B Frigate
        _fixed.add("9_82");  // Red Squadron 4
        _fixed.add("9_83");  // Red Squadron 7
        _fixed.add("1_146"); // X-wing
        _fixed.add("1_146"); // X-wing
        _fixed.add("1_147"); // Y-wing
        _fixed.add("8_85");  // BlasTech E-11B Blaster Rifle
        _fixed.add("8_85");  // BlasTech E-11B Blaster Rifle
        _fixed.add("9_87");  // Concussion Missiles
        _fixed.add("9_87");  // Concussion Missiles
        _fixed.add("9_88");  // Enhanced Proton Torpedoes
        _fixed.add("7_159"); // Intruder Missile
        _fixed.add("7_159"); // Intruder Missile

        filterNonExistingCards(_fixed);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_LIGHT_DECK;
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
