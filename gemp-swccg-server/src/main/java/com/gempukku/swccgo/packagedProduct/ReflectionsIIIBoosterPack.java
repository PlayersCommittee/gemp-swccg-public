package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Reflections III booster pack.
 */
public class ReflectionsIIIBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _premiereSetRarity;
    private SetRarity _aNewHopeSetRarity;
    private SetRarity _hothSetRarity;
    private SetRarity _dagobahSetRarity;
    private SetRarity _cloudCitySetRarity;
    private SetRarity _jabbasPalaceRarity;
    private SetRarity _specialEditionSetRarity;
    private SetRarity _endorEditionSetRarity;
    private SetRarity _reflectionsIIISetRarity;

    /**
     * Creates a Reflections III booster pack.
     * @param library the blueprint library
     */
    public ReflectionsIIIBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _premiereSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.PREMIERE.getSetNumber()));
        _aNewHopeSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.A_NEW_HOPE.getSetNumber()));
        _hothSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.HOTH.getSetNumber()));
        _dagobahSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.DAGOBAH.getSetNumber()));
        _cloudCitySetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.CLOUD_CITY.getSetNumber()));
        _jabbasPalaceRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.JABBAS_PALACE.getSetNumber()));
        _specialEditionSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.SPECIAL_EDITION.getSetNumber()));
        _endorEditionSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.ENDOR.getSetNumber()));
        _reflectionsIIISetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.REFLECTIONS_III.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.REFLECTIONS_III_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.REFLECTIONS_III_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCard(result, 14);
        addRandomFoilCard(result, 1);
        addRandomReflectionsIIICard(result, 3);
        return result;
    }

    /**
     * Adds random Premiere cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_premiereSetRarity.getAllCards());
        possibleCards.addAll(_aNewHopeSetRarity.getAllCards());
        possibleCards.addAll(_hothSetRarity.getAllCards());
        possibleCards.addAll(_cloudCitySetRarity.getAllCards());
        possibleCards.addAll(_dagobahSetRarity.getAllCards());
        possibleCards.addAll(_jabbasPalaceRarity.getAllCards());
        possibleCards.addAll(_specialEditionSetRarity.getAllCards());
        possibleCards.addAll(_endorEditionSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Reflections III cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomReflectionsIIICard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_reflectionsIIISetRarity.getCardsOfRarity(Rarity.PM));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random foil cards (for Reflections III) to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomFoilCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();

        // Very Rare foils
        for (int i=0; i<3; ++i) {
            possibleCards.add("9_92");   // Battle Deployment
            possibleCards.add("7_169");  // Brangus Glee
            possibleCards.add("12_147"); // Dioxis
            possibleCards.add("1_172");  // Dr. Evazan
            possibleCards.add("1_179");  // Grand Moff Tarkin
            possibleCards.add("112_13"); // Mercenary Pilot
            possibleCards.add("112_14"); // Mighty Jabba
            possibleCards.add("11_64");  // Sith Probe Droid
            possibleCards.add("1_48");   // Disarmed
            possibleCards.add("112_17"); // Power Of The Hutt
            possibleCards.add("11_77");  // You May Start Your Landing
            possibleCards.add("11_79");  // Boonta Eve Podrace
            possibleCards.add("102_8");  // Gravity Shadow
            possibleCards.add("1_248");  // I Have You Now
            possibleCards.add("9_137");  // Imperial Command
            possibleCards.add("1_271");  // The Circle Is Now Complete
            possibleCards.add("3_138");  // Trample
            possibleCards.add("12_162"); // Vote Now!
            possibleCards.add("11_92");  // Tatooine: Desert Landing Site
            possibleCards.add("11_99");  // Maul's Lightsaber
            possibleCards.add("1_324");  // Vader's Lightsaber
            possibleCards.add("12_2");   // Captain Panaka
            possibleCards.add("10_3");   // Chewbacca, Protector
            possibleCards.add("11_4");   // Jar Jar Binks
            possibleCards.add("112_5");  // Palace Raider
            possibleCards.add("111_5");  // Prisoner 2187
            possibleCards.add("12_24");  // Ric Olie
            possibleCards.add("11_12");  // Shmi Skywalker
            possibleCards.add("12_30");  // Supreme Chancellor Valorum
            possibleCards.add("12_35");  // Yoda, Senior Council Member
            possibleCards.add("111_1");  // A New Secret Base
            possibleCards.add("11_16");  // Brisky Morning Munchen
            possibleCards.add("111_3");  // Echo Base Garrison
            possibleCards.add("112_7");  // Seeking An Audience
            possibleCards.add("11_25");  // I Did it!
            possibleCards.add("11_33");  // End Of A Reign
            possibleCards.add("12_66");  // Rebel Artillery
            possibleCards.add("12_71");  // Vote Now!
            possibleCards.add("12_76");  // Coruscant: Jedi Council Chamber
            possibleCards.add("10_20");  // Pulsar Skate
            possibleCards.add("12_91");  // Queen's Royal Starship
            possibleCards.add("12_92");  // Radiant VII
            possibleCards.add("3_71");   // Anakin's Lightsaber
        }

        // Super Rare foils
        for (int i=0; i<2; ++i) {
            possibleCards.add("10_31");  // Arica
            possibleCards.add("11_52");  // Aurra Sing (AI)
            possibleCards.add("10_33");  // Captain Gilad Pellaeon
            possibleCards.add("11_55");  // Darth Maul (AI)
            possibleCards.add("12_104"); // Destroyer Droid
            possibleCards.add("10_37");  // Dr. Evazan & Ponda Baba
            possibleCards.add("10_40");  // Grand Admiral Thrawn
            possibleCards.add("10_41");  // Guri
            possibleCards.add("12_110"); // Lott Dod
            possibleCards.add("12_112"); // Nute Gunray
            possibleCards.add("12_115"); // P-60
            possibleCards.add("10_45");  // Prince Xizor
            possibleCards.add("10_48");  // Snoova
            possibleCards.add("12_119"); // TC-14
            possibleCards.add("10_53");  // Vigo
            possibleCards.add("11_66");  // Watto (AI)
            possibleCards.add("14_94");  // After Her!
            possibleCards.add("14_98");  // Naboo Occupation
            possibleCards.add("8_127");  // Ominous Rumors
            possibleCards.add("12_140"); // The Phantom Menace (AI)
            possibleCards.add("12_148"); // Imperial Artillery
            possibleCards.add("12_153"); // Maul Strikes
            possibleCards.add("14_105"); // Rolling, Rolling, Rolling
            possibleCards.add("11_97");  // Sebulba's Podracer
            possibleCards.add("12_183"); // Maul's Sith Infiltrator (AI)
            possibleCards.add("10_49");  // Stinger
            possibleCards.add("10_54");  // Virago
            possibleCards.add("10_2");   // Artoo & Threepio
            possibleCards.add("10_5");   // Corran Horn
            possibleCards.add("10_6");   // Dash Rendar
            possibleCards.add("112_3");  // Lando With Vibro-Ax
            possibleCards.add("1_19");   // Luke Skywalker
            possibleCards.add("10_10");  // Luke Skywalker, Rebel Scout
            possibleCards.add("12_13");  // Mace Windu
            possibleCards.add("12_17");  // Master Qui-Gon (AI)
            possibleCards.add("10_12");  // Mirax Terrik
            possibleCards.add("11_7");   // Obi-Wan Kenobi, Padawan Learner (AI)
            possibleCards.add("1_21");   // Obi-Wan Kenobi
            possibleCards.add("10_18");  // Owen Lars & Beru Lars
            possibleCards.add("11_9");   // Padme Naberrie (AI)
            possibleCards.add("10_24");  // Talon Karrde
            possibleCards.add("2_23");   // Wedge Antilles
            possibleCards.add("10_15");  // Obi-Wan's Journal
            possibleCards.add("14_37");  // They Win This Round
            possibleCards.add("11_24");  // Boonta Eve Podrace
            possibleCards.add("14_42");  // Gimme A Lift
            possibleCards.add("7_9");    // Harvest
            possibleCards.add("9_54");   // Rebel Leadership
            possibleCards.add("11_47");  // Anakin's Podracer
            possibleCards.add("10_17");  // Outrider
            possibleCards.add("14_62");  // Amidala's Blaster
            possibleCards.add("1_157");  // Obi-Wan's Lightsaber
            possibleCards.add("11_50");  // Qui-Gon Jinn's Lightsaber (AI)
        }

        // Ultra Rare foils
        for (int i=0; i<1; ++i) {
            possibleCards.add("12_102");   // Darth Maul, Young Apprentice (AI)
            possibleCards.add("12_114");   // P-59
            possibleCards.add("11_11");    // Qui-Gon Jinn (AI)
            possibleCards.add("11_14");    // Threepio With His Parts Showing (AI)
        }

        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), true);
    }
}
