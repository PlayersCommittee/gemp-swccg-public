package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Reflections booster pack.
 */
public class ReflectionsBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _premiereSetRarity;
    private SetRarity _aNewHopeSetRarity;
    private SetRarity _hothSetRarity;
    private SetRarity _dagobahSetRarity;
    private SetRarity _cloudCitySetRarity;
    private SetRarity _jabbasPalaceRarity;
    private SetRarity _specialEditionSetRarity;

    /**
     * Creates a Reflections booster pack.
     * @param library the blueprint library
     */
    public ReflectionsBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _premiereSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.PREMIERE.getSetNumber()));
        _aNewHopeSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.A_NEW_HOPE.getSetNumber()));
        _hothSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.HOTH.getSetNumber()));
        _dagobahSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.DAGOBAH.getSetNumber()));
        _cloudCitySetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.CLOUD_CITY.getSetNumber()));
        _jabbasPalaceRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.JABBAS_PALACE.getSetNumber()));
        _specialEditionSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.SPECIAL_EDITION.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.REFLECTIONS_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.REFLECTIONS_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomSpecialEditionCard(result, 3);
        addRandomJabbasPalaceCard(result, 4);
        addRandomCloudCityCard(result, 4);
        addRandomPremiereCard(result, 3);
        addRandomFoilCard(result, 1);
        addRandomANewHopeCard(result, 1);
        addRandomHothCard(result, 1);
        addRandomDagobahCard(result, 1);
        return result;
    }

    /**
     * Adds random Premiere cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomPremiereCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_premiereSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, count), false);
    }

    /**
     * Adds random A New Hope cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomANewHopeCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_aNewHopeSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, count), false);
    }

    /**
     * Adds random Hoth cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomHothCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_hothSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, count), false);
    }

    /**
     * Adds random Dagobah cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomDagobahCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_dagobahSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, count), false);
    }

    /**
     * Adds random Cloud City cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCloudCityCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_cloudCitySetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Jabba's Palace cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomJabbasPalaceCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_jabbasPalaceRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Special Edition cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomSpecialEditionCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_specialEditionSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random foil cards (for Reflections) to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomFoilCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();

        // Very Rare foils
        for (int i=0; i<4; ++i) {
            possibleCards.add("4_91");   // 4-LOM
            possibleCards.add("3_82");   // Admiral Ozzel
            possibleCards.add("6_95");   // Bane Malar
            possibleCards.add("6_98");   // Bib Fortuna
            possibleCards.add("4_92");   // Bossk
            possibleCards.add("4_100");  // Dengar
            possibleCards.add("1_174");  // DS-61-3
            possibleCards.add("2_89");   // Greedo
            possibleCards.add("4_101");  // IG-88
            possibleCards.add("2_93");   // IT-O
            possibleCards.add("7_182");  // Jabba
            possibleCards.add("7_187");  // Lobot (Dark)
            possibleCards.add("6_122");  // Salacious Crumb
            possibleCards.add("1_195");  // Tonnika Sisters
            possibleCards.add("2_107");  // U-3PO
            possibleCards.add("4_107");  // Zuckuss
            possibleCards.add("6_139");  // Rancor
            possibleCards.add("3_93");   // Wampa
            possibleCards.add("1_215");  // Expand The Empire
            possibleCards.add("4_135");  // Visage Of The Emperor
            possibleCards.add("2_130");  // Commence Primary Ignition
            possibleCards.add("5_129");  // Epic Duel
            possibleCards.add("1_227");  // Presence of the Force
            possibleCards.add("5_154");  // Slip Sliding Away
            possibleCards.add("1_271");  // The Circle Is Now Complete
            possibleCards.add("4_166");  // Avenger
            possibleCards.add("1_299");  // Black 2
            possibleCards.add("7_301");  // Bossk in Hound's Tooth
            possibleCards.add("2_152");  // Conquest
            possibleCards.add("7_302");  // Death Squadron Star Destroyer
            possibleCards.add("4_169");  // IG-2000
            possibleCards.add("4_170");  // Mist Hunter
            possibleCards.add("5_175");  // Obsidian 7
            possibleCards.add("5_176");  // Obsidian 8
            possibleCards.add("109_10"); // Punishing One
            possibleCards.add("3_152");  // Stalker
            possibleCards.add("3_153");  // Tyrant
            possibleCards.add("7_310");  // Vengeance
            possibleCards.add("3_154");  // Blizzard 1
            possibleCards.add("3_155");  // Blizzard 2
            possibleCards.add("3_156");  // Blizzard Scout 1
            possibleCards.add("6_172");  // Jabba's Sail Barge
            possibleCards.add("5_179");  // Boba Fett's Blaster Rifle
            possibleCards.add("2_161");  // Superlaser
            possibleCards.add("3_1");    // 2-1B
            possibleCards.add("6_3");    // Artoo
            possibleCards.add("1_3");    // Biggs Darklighter
            possibleCards.add("2_2");    // Brainiac
            possibleCards.add("3_3");    // Commander Luke Skywalker
            possibleCards.add("1_8");    // Dutch
            possibleCards.add("5_6");    // Lobot (Light)
            possibleCards.add("6_29");   // Oola
            possibleCards.add("5_7");    // Princess Leia
            possibleCards.add("7_35");   // Princess Organa
            possibleCards.add("1_29");   // Red Leader
            possibleCards.add("6_42");   // Tamtel Skreej
            possibleCards.add("7_48");   // TK-422
            possibleCards.add("2_23");   // Wedge Antilles
            possibleCards.add("4_11");   // Landing Claw
            possibleCards.add("7_57");   // Coruscant Celebration
            possibleCards.add("1_46");   // Death Star Plans
            possibleCards.add("5_24");   // Haven
            possibleCards.add("1_54");   // Lightsaber Proficiency
            possibleCards.add("7_70");   // Mechanical Failure
            possibleCards.add("4_33");   // Reflection
            possibleCards.add("1_62");   // Revolution
            possibleCards.add("5_29");   // Uncontrollable Fury
            possibleCards.add("4_42");   // What Is Thy Bidding, My Master?
            possibleCards.add("2_42");   // Attack Run
            possibleCards.add("7_82");   // All Wings Report In
            possibleCards.add("1_110");  // Skywalkers
            possibleCards.add("4_78");   // It Is The Future You See
            possibleCards.add("7_113");  // Cloud City: Downtown Plaza
            possibleCards.add("5_80");   // Cloud City: Guest Quarters
            possibleCards.add("4_89");   // Dagobah: Yoda's Hut
            possibleCards.add("7_117");  // Death Star (Light)
            possibleCards.add("2_62");   // Death Star: Trench
            possibleCards.add("7_123");  // Rendezvous Point
            possibleCards.add("2_70");   // Red 2
            possibleCards.add("2_71");   // Red 5
            possibleCards.add("5_87");   // Redemption
            possibleCards.add("7_149");  // Spiral
            possibleCards.add("2_73");   // Tantive IV
            possibleCards.add("3_66");   // Rogue 1
            possibleCards.add("3_68");   // Rogue 3
            possibleCards.add("3_71");   // Anakin's Lightsaber
            possibleCards.add("1_157");  // Obi-Wan's Lightsaber
        }

        // Super Rare foils
        for (int i=0; i<2; ++i) {
            possibleCards.add("5_91");   // Boba Fett (Cloud City)
            possibleCards.add("7_175");  // Darth Vader, Dark Lord Of The Sith
            possibleCards.add("3_87");   // General Veers
            possibleCards.add("1_179");  // Grand Moff Tarkin
            possibleCards.add("6_109");  // Jabba The Hutt
            possibleCards.add("5_99");   // Lando Calrissian (Dark)
            possibleCards.add("2_143");  // Death Star
            possibleCards.add("1_301");  // Devastator
            possibleCards.add("4_167");  // Executor
            possibleCards.add("5_177");  // Slave I
            possibleCards.add("1_306");  // Vader's Custom TIE
            possibleCards.add("1_324");  // Vader's Lightsaber
            possibleCards.add("7_4");    // Ben Kenobi
            possibleCards.add("1_5");    // C-3PO (See-Threepio)
            possibleCards.add("5_1");    // Captain Han Solo
            possibleCards.add("2_3");    // Chewbacca
            possibleCards.add("1_11");   // Han Solo
            possibleCards.add("5_5");    // Lando Calrissian (Light)
            possibleCards.add("1_17");   // Leia Organa
            possibleCards.add("1_21");   // Obi-Wan Kenobi
            possibleCards.add("6_32");   // Princess Leia Organa
            possibleCards.add("2_14");   // R2-D2 (Artoo-Detoo)
            possibleCards.add("4_1");    // Son Of Skywalker
            possibleCards.add("4_2");    // Yoda
            possibleCards.add("1_143");  // Millennium Falcon
        }

        // Ultra Rare foils
        for (int i=0; i<1; ++i) {
            possibleCards.add("1_168");   // Darth Vader
            possibleCards.add("1_19");    // Luke Skywalker
        }

        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), true);
    }
}
