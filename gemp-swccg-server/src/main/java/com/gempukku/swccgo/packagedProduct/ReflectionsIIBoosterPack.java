package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Reflections II booster pack.
 */
public class ReflectionsIIBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _premiereSetRarity;
    private SetRarity _aNewHopeSetRarity;
    private SetRarity _hothSetRarity;
    private SetRarity _cloudCitySetRarity;
    private SetRarity _jabbasPalaceRarity;
    private SetRarity _specialEditionSetRarity;
    private List<String> _reflectionsIIComboCards = new ArrayList<String>();
    private List<String> _reflectionsIIExpandedUniverse = new ArrayList<String>();

    /**
     * Creates a Reflections II booster pack.
     * @param library the blueprint library
     */
    public ReflectionsIIBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _premiereSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.PREMIERE.getSetNumber()));
        _aNewHopeSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.A_NEW_HOPE.getSetNumber()));
        _hothSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.HOTH.getSetNumber()));
        _cloudCitySetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.CLOUD_CITY.getSetNumber()));
        _jabbasPalaceRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.JABBAS_PALACE.getSetNumber()));
        _specialEditionSetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.SPECIAL_EDITION.getSetNumber()));
        SetRarity reflectionsIISetRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.REFLECTIONS_II.getSetNumber()));

        for (String blueprintId : reflectionsIISetRarity.getCardsOfRarity(Rarity.PM)) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.isComboCard()) {
                    _reflectionsIIComboCards.add(blueprintId);
                }
                else {
                    _reflectionsIIExpandedUniverse.add(blueprintId);
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
        return ProductName.REFLECTIONS_II_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.REFLECTIONS_II_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomPremiereCard(result, 3);
        addRandomJabbasPalaceCard(result, 3);
        addRandomFoilCard(result, 1);
        addRandomCloudCityCard(result, 3);
        addRandomReflectionsIICards(result);
        addRandomANewHopeOrHothCard(result, 1);
        addRandomSpecialEditionCard(result, 3);
        addRandomPremiereCard(result, 2);
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
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random A New Hope or Hoth cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomANewHopeOrHothCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_aNewHopeSetRarity.getAllCards());
        possibleCards.addAll(_hothSetRarity.getAllCards());
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
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
     * Adds random Reflections II cards to the list.
     * @param result the list of cards in the pack
     */
    private void addRandomReflectionsIICards(List<CardCollection.Item> result) {
        List<String> possibleComboCards = new ArrayList<String>();
        possibleComboCards.addAll(_reflectionsIIComboCards);
        filterNonExistingCards(possibleComboCards);
        Collections.shuffle(possibleComboCards, _random);
        addCards(result, possibleComboCards.subList(0, Math.min(possibleComboCards.size(), 1)), false);

        List<String> possibleExpandedUniverseCards = new ArrayList<String>();
        possibleExpandedUniverseCards.addAll(_reflectionsIIExpandedUniverse);
        filterNonExistingCards(possibleExpandedUniverseCards);
        Collections.shuffle(possibleExpandedUniverseCards, _random);
        addCards(result, possibleExpandedUniverseCards.subList(0, Math.min(possibleExpandedUniverseCards.size(), 1)), false);
    }

    /**
     * Adds random foil cards (for Reflections II) to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomFoilCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();

        // Very Rare foils
        for (int i=0; i<3; ++i) {
            possibleCards.add("9_94");   // Fighters Coming In
            possibleCards.add("7_168");  // Boelo
            possibleCards.add("106_11"); // Chall Bekan
            possibleCards.add("8_94");   // Commander Igar
            possibleCards.add("9_110");  // Janus Greejatus
            possibleCards.add("9_118");  // Myn Kyneugh
            possibleCards.add("9_120");  // Sim Aloo
            possibleCards.add("4_116");  // Bad Feeling Have I
            possibleCards.add("1_222");  // Lateral Damage
            possibleCards.add("6_149");  // Scum And Villainy
            possibleCards.add("7_241");  // Sienar Fleet Systems
            possibleCards.add("101_6");  // Vader's Obsession
            possibleCards.add("9_147");  // Death Star II: Throne Room
            possibleCards.add("4_161");  // Executor: Holotheatre
            possibleCards.add("4_163");  // Executor: Meditation Chamber
            possibleCards.add("3_150");  // Hoth: Wampa Cave
            possibleCards.add("2_147");  // Kiffex (Dark)
            possibleCards.add("106_10"); // Black Squadron TIE
            possibleCards.add("110_8");  // IG-88 In IG-2000
            possibleCards.add("106_1");  // Arleil Schous
            possibleCards.add("7_44");   // Tawss Khaa
            possibleCards.add("5_23");   // Frozen Assets
            possibleCards.add("7_62");   // Goo Nee Tay
            possibleCards.add("1_55");   // Mantellian Savrip
            possibleCards.add("4_30");   // Order To Engage
            possibleCards.add("101_3");  // Run Luke, Run!
            possibleCards.add("104_2");  // Lone Rogue
            possibleCards.add("6_83");   // Kiffex (Light)
            possibleCards.add("9_64");   // Blue Squadron B-wing
            possibleCards.add("103_1");  // Gold Leader In Gold 1
            possibleCards.add("9_73");   // Green Squadron A-wing
            possibleCards.add("9_76");   // Liberty
            possibleCards.add("103_2");  // Red Leader In Red 1
            possibleCards.add("106_9");  // Z-95 Headhunter
        }

        // Super Rare foils
        for (int i=0; i<2; ++i) {
            possibleCards.add("109_6");  // 4-LOM With Concussion Rifle
            possibleCards.add("9_98");   // Admiral Piett
            possibleCards.add("9_99");   // Baron Soontir Fel
            possibleCards.add("110_5");  // Bossk With Mortar Gun
            possibleCards.add("108_6");  // Darth Vader With Lightsaber
            possibleCards.add("110_7");  // Dengar With Blaster Carbine
            possibleCards.add("1_171");  // Djas Puhr
            possibleCards.add("109_11"); // IG-88 With Riot Gun
            possibleCards.add("110_9");  // Jodo Kast
            possibleCards.add("9_117");  // Moff Jerjerrod
            possibleCards.add("7_195");  // Outer Rim Scout
            possibleCards.add("9_136");  // Force Lightning
            possibleCards.add("3_138");  // Trample
            possibleCards.add("104_7");  // Walker Garrison
            possibleCards.add("2_143");  // Death Star
            possibleCards.add("9_142");  // Death Star II
            possibleCards.add("109_8");  // Boba Fett In Slave I
            possibleCards.add("9_154");  // Chimaera
            possibleCards.add("109_10"); // Dengar In Punishing One
            possibleCards.add("106_13"); // Dreadnaught-Class Heavy Cruiser
            possibleCards.add("9_157");  // Flagship Executor
            possibleCards.add("9_172");  // The Emperor's Shield
            possibleCards.add("9_173");  // The Emperor's Sword
            possibleCards.add("110_12"); // Zuckuss In Mist Hunter
            possibleCards.add("104_5");  // Imperial Walker
            possibleCards.add("8_172");  // Tempest Scout 1
            possibleCards.add("9_178");  // Darth Vader's Lightsaber
            possibleCards.add("110_11"); // Mara Jade's Lightsaber
            possibleCards.add("9_1");    // Capital Support
            possibleCards.add("9_6");    // Admiral Ackbar
            possibleCards.add("2_2");    // Brainiac
            possibleCards.add("109_1");  // Chewie With Blaster Rifle
            possibleCards.add("8_3");    // Chief Chirpa
            possibleCards.add("9_13");   // General Calrissian
            possibleCards.add("8_14");   // General Crix Madine
            possibleCards.add("1_15");   // Kal'Falnl C'ndros
            possibleCards.add("102_3");  // Leia
            possibleCards.add("108_3");  // Luke With Lightsaber
            possibleCards.add("7_32");   // Melas
            possibleCards.add("8_23");   // Orrimaarko
            possibleCards.add("110_3");  // See-Threepio
            possibleCards.add("9_31");   // Wedge Antilles, Red Squadron Leader
            possibleCards.add("8_32");   // Wicket
            possibleCards.add("3_32");   // Bacta Tank
            possibleCards.add("3_34");   // Echo Base Operations
            possibleCards.add("1_52");   // Kessel Run
            possibleCards.add("1_76");   // Don't Get Cocky
            possibleCards.add("1_82");   // Gift Of The Mentor
            possibleCards.add("5_69");   // Smoke Screen
            possibleCards.add("1_138");  // Yavin 4: Massassi Throne Room
            possibleCards.add("111_2");  // Artoo-Detoo In Red 5
            possibleCards.add("9_65");   // B-wing Attack Squadron
            possibleCards.add("9_68");   // Gold Squadron 1
            possibleCards.add("106_4");  // Gold Squadron Y-wing
            possibleCards.add("9_74");   // Home One
            possibleCards.add("9_75");   // Independence
            possibleCards.add("109_2");  // Lando In Millennium Falcon
            possibleCards.add("9_81");   // Red Squadron 1
            possibleCards.add("106_7");  // Red Squadron X-wing
            possibleCards.add("7_150");  // X-wing Assault Squadron
            possibleCards.add("104_3");  // Rebel Snowspeeder
            possibleCards.add("9_90");   // Luke's Lightsaber
        }

        // Ultra Rare foils
        for (int i=0; i<1; ++i) {
            possibleCards.add("9_109");   // Emperor Palpatine
            possibleCards.add("9_113");   // Lord Vader
            possibleCards.add("110_10");  // Mara Jade, The Emperor's Hand
            possibleCards.add("9_24");    // Luke Skywalker, Jedi Knight
        }

        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), true);
    }
}
