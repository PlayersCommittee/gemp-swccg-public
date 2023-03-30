package com.gempukku.swccgo.league;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.packagedProduct.ProductName;

import java.util.*;

/**
 * Contains the sealed league product to use for sealed leagues.
 */
public class SealedLeagueProduct {
    private Map<String, List<CardCollection>> _collections = new HashMap<String, List<CardCollection>>();
    private Map<String, List<List<PoolIntPair>>> _fixedFromPool = new HashMap<>();
    /**
     * Creates the sealed league project to use for sealed leagues.
     */
    public SealedLeagueProduct() {
        createPremiereAnhSealed();
        createJpSealed();
        createEndorDsIISealed();
        createEpisodeISealed();
        createAllOfTheJediSealed();
        createNoveltySealed();
        createWattosCubeWithObjective();
        createWattosCubeWithFixed();
    }

    /**
     * Creates the collection of products to use for Premiere - ANH sealed.
     */
    private void createPremiereAnhSealed() {
        List<CardCollection> premiereAnhSealed = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem(ProductName.JEDI_PACK, 1);
        firstWeek.addItem(ProductName.OFFICIAL_TOURNAMENT_SEALED_DECK, 2);
        firstWeek.addItem("101_1", 1);
        firstWeek.addItem("101_2", 1);
        firstWeek.addItem("101_3", 1);
        firstWeek.addItem("101_4", 1);
        firstWeek.addItem("101_5", 1);
        firstWeek.addItem("101_6", 1);
        firstWeek.addItem("2_33", 1);
        firstWeek.addItem("2_41", 1);
        firstWeek.addItem("2_127", 1);
        firstWeek.addItem("2_128", 1);
        premiereAnhSealed.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem(ProductName.PREMIERE_BOOSTER_PACK, 2);
        secondWeek.addItem(ProductName.A_NEW_HOPE_BOOSTER_PACK, 4);
        premiereAnhSealed.add(secondWeek);

        _collections.put(SealedLeagueType.PREMIERE_ANH_SEALED.getSealedCode(), premiereAnhSealed);
    }

    /**
     * Creates the collection of products to use for Jabba's Palace sealed.
     */
    private void createJpSealed() {
        List<CardCollection> jpSealed = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem(ProductName.JABBAS_PALACE_SEALED_DECK, 2);
        jpSealed.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)Enhanced Jabba's Palace Pack Choice", 1);
        jpSealed.add(secondWeek);

        _collections.put(SealedLeagueType.JP_SEALED.getSealedCode(), jpSealed);
    }

    /**
     * Creates the collection of products to use for Endor - Death Star II sealed.
     */
    private void createEndorDsIISealed() {
        List<CardCollection> endorDsIISealed = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem(ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_DARK_DECK, 1);
        firstWeek.addItem(ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_LIGHT_DECK, 1);
        firstWeek.addItem(ProductName.ENDOR_BOOSTER_PACK_NO_RANDOM_FOIL, 4);
        endorDsIISealed.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem(ProductName.DEATH_STAR_II_BOOSTER_PACK, 4);
        endorDsIISealed.add(secondWeek);

        _collections.put(SealedLeagueType.ENDOR_DSII_SEALED.getSealedCode(), endorDsIISealed);
    }

    /**
     * Creates the collection of products to use for Episode I sealed.
     */
    private void createEpisodeISealed() {
        List<CardCollection> episodeISealed = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        //first week
        firstWeek.addItem(ProductName.TATOOINE_BOOSTER_PACK_EPISODE_I_ONLY, 5);
        firstWeek.addItem(ProductName.CORUSCANT_BOOSTER_PACK_EPISODE_I_ONLY,5);
        firstWeek.addItem(ProductName.THEED_PALACE_BOOSTER_PACK,5);
        firstWeek.addItem("13_74",1); //Lord Maul
        firstWeek.addItem("13_33",1); //Obi-Wan Kenobi, Jedi Knight
        firstWeek.addItem("14_114",1); //Blockade Flagship
        firstWeek.addItem("12_184",2); //Trade Federation Battleship
        firstWeek.addItem("12_91",1); //Queen's Royal Starship
        firstWeek.addItem("12_93",2); //Republic Cruiser
        firstWeek.addItem("14_82",1); //Nute Gunray, Neimoidian Viceroy
        firstWeek.addItem("11_52",1); //Aurra Sing
        firstWeek.addItem("12_107",1); //Grotto Werribee
        firstWeek.addItem("14_73",1); //Battle Droid Officer
        firstWeek.addItem("12_118",1); //Security Battle Droid
        firstWeek.addItem("14_26",1); //Queen Amidala
        firstWeek.addItem("12_20",1); //Phylo Gandish
        firstWeek.addItem("14_12",1); //Gungan General
        firstWeek.addItem("12_7",1); //Gungan Warrior
        firstWeek.addItem("12_154",1); //Neimoidian Advisor
        firstWeek.addItem("12_136",1); //Mind Tricks Don't Work On Me
        firstWeek.addItem("12_62",1); //Mindful Of The Future
        firstWeek.addItem("12_50",1); //We're Leaving
        firstWeek.addItem("11_93",1); //Tatooine: Mos Espa (Dark)
        firstWeek.addItem("11_43",1); //Tatooine: Mos Espa (Light)
        firstWeek.addItem("12_175",1); //Tatooine (Dark)
        firstWeek.addItem("12_84",1); //Tatooine (Light)
        firstWeek.addItem("12_169",1); //Naboo (Dark)
        firstWeek.addItem("12_78",1); //Naboo (Light)
        firstWeek.addItem("12_172",1); //Naboo: Theed Palace Courtyard (Dark)
        firstWeek.addItem("12_81",1); //Naboo: Theed Palace Courtyard (Light)
        firstWeek.addItem("12_167",1); //Coruscant: Galactic Senate (Dark)
        firstWeek.addItem("12_75",1); //Coruscant: Galactic Senate (Light)

        episodeISealed.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        //second week
        secondWeek.addItem(ProductName.TATOOINE_BOOSTER_PACK_EPISODE_I_ONLY, 3);
        secondWeek.addItem(ProductName.CORUSCANT_BOOSTER_PACK_EPISODE_I_ONLY,3);
        secondWeek.addItem(ProductName.THEED_PALACE_BOOSTER_PACK,3);
        episodeISealed.add(secondWeek);

        _collections.put(SealedLeagueType.EPISODE_I_SEALED.getSealedCode(), episodeISealed);
    }

    /**
     * Creates the collection of products to use for All Of The Jedi sealed
     */
    private void createAllOfTheJediSealed() {

        //  All Of The Jedi (2 series)
        //  series 1 = 2x each Decipher booster pack, some locations pulled from fixed pools
        //  series 2 = 1x each Decipher booster pack

        List<CardCollection> allofthejediSealed = new ArrayList<CardCollection>();
        List<List<PoolIntPair>> pools = new LinkedList<>();


        MutableCardCollection firstWeek = new DefaultCardCollection();
        //first week
        firstWeek.addItem(ProductName.PREMIERE_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.A_NEW_HOPE_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.HOTH_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.DAGOBAH_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.CLOUD_CITY_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.JABBAS_PALACE_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.SPECIAL_EDITION_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.ENDOR_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.DEATH_STAR_II_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.TATOOINE_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.CORUSCANT_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.THEED_PALACE_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.REFLECTIONS_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.REFLECTIONS_II_BOOSTER_PACK, 2);
        firstWeek.addItem(ProductName.REFLECTIONS_III_BOOSTER_PACK, 2);

        firstWeek.addItem("6_160",1); //Twi'lek Advisor
        firstWeek.addItem("6_77",1); //The Signal


        allofthejediSealed.add(firstWeek);

        List<String> darkSitePool = new LinkedList<>();
        List<String> darkSystemPool = new LinkedList<>();
        List<String> lightSitePool = new LinkedList<>();
        List<String> lightSystemPool = new LinkedList<>();

        darkSitePool.add("5_166"); //Cloud City: Carbonite Chamber
        darkSitePool.add("7_269"); //Cloud City: Casino
        darkSitePool.add("5_167"); //Cloud City: Chasm Walkway
        darkSitePool.add("7_273"); //Cloud City: Upper Walkway
        darkSitePool.add("7_274"); //Cloud City: West Gallery
        darkSitePool.add("8_158"); //Endor: Ancient Forest
        darkSitePool.add("8_161"); //Endor: Dark Forest
        darkSitePool.add("8_162"); //Endor: Dense Forest
        darkSitePool.add("112_12"); //Jabba's Palace: Lower Passages
        darkSitePool.add("12_172"); //Naboo: Theed Palace Courtyard
        darkSitePool.add("12_174"); //Naboo: Theed Palace Throne Room
        darkSitePool.add("1_295"); //Tatooine: Mos Eisley
        darkSitePool.add("11_93"); //Tatooine: Mos Espa
        darkSitePool.add("12_178"); //Tatooine: Watto's Junkyard
        darkSystemPool.add("4_154"); //Anoat
        darkSystemPool.add("5_164"); //Bespin
        darkSystemPool.add("8_156"); //Carida
        darkSystemPool.add("106_12"); //Corulag
        darkSystemPool.add("12_165"); //Coruscant
        darkSystemPool.add("7_283"); //Fondor
        darkSystemPool.add("3_143"); //Hoth
        darkSystemPool.add("12_168"); //Malastare
        darkSystemPool.add("9_149"); //Mon Calamari
        darkSystemPool.add("6_168"); //Nal Hutta
        darkSystemPool.add("4_164"); //Raithal
        darkSystemPool.add("7_287"); //Rendili
        darkSystemPool.add("1_289"); //Tatooine
        darkSystemPool.add("12_175"); //Tatooine
        darkSystemPool.add("1_296"); //Yavin 4
        lightSitePool.add("7_111"); //Cloud City: Casino
        lightSitePool.add("5_80"); //Cloud City: Guest Quarters
        lightSitePool.add("7_114"); //Cloud City: North Corridor
        lightSitePool.add("7_115"); //Cloud City: West Gallery
        lightSitePool.add("8_72"); //Endor: Dense Forest
        lightSitePool.add("8_75"); //Endor: Hidden Forest Trail
        lightSitePool.add("8_77"); //Endor: Rebel Landing Site (Forest)
        lightSitePool.add("112_2"); //Jabba's Palace: Antechamber
        lightSitePool.add("6_81"); //Jabba's Palace: Audience Chamber
        lightSitePool.add("12_81"); //Naboo: Theed Palace Courtyard
        lightSitePool.add("12_83"); //Naboo: Theed Palace Throne Room
        lightSitePool.add("112_9"); //Tatooine: Hutt Trade Route (Desert)
        lightSitePool.add("7_131"); //Tatooine: Jabba's Palace
        lightSitePool.add("1_132"); //Tatooine: Lars' Moisture Farm
        lightSitePool.add("1_133"); //Tatooine: Mos Eisley
        lightSitePool.add("11_43"); //Tatooine: Mos Espa
        lightSystemPool.add("4_80"); //Anoat
        lightSystemPool.add("5_76"); //Bespin
        lightSystemPool.add("7_110"); //Bothawui
        lightSystemPool.add("8_67"); //Chandrila
        lightSystemPool.add("106_2"); //Corulag
        lightSystemPool.add("12_73"); //Coruscant
        lightSystemPool.add("8_68"); //Endor
        lightSystemPool.add("3_55"); //Hoth
        lightSystemPool.add("2_64"); //Kashyyyk
        lightSystemPool.add("12_77"); //Malastare
        lightSystemPool.add("9_59"); //Mon Calamari
        lightSystemPool.add("4_90"); //Raithal
        lightSystemPool.add("9_60"); //Sullust
        lightSystemPool.add("1_127"); //Tatooine
        lightSystemPool.add("12_84"); //Tatooine
        lightSystemPool.add("1_135"); //Yavin 4


        List<PoolIntPair> firstWeekPools = new LinkedList<>();
        firstWeekPools.add(new PoolIntPair(darkSitePool, 1));
        firstWeekPools.add(new PoolIntPair(darkSystemPool, 1));
        firstWeekPools.add(new PoolIntPair(lightSitePool, 1));
        firstWeekPools.add(new PoolIntPair(lightSystemPool, 1));

        pools.add(firstWeekPools);


        MutableCardCollection secondWeek = new DefaultCardCollection();
        //second week
        secondWeek.addItem(ProductName.PREMIERE_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.A_NEW_HOPE_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.HOTH_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.DAGOBAH_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.CLOUD_CITY_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.JABBAS_PALACE_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.SPECIAL_EDITION_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.ENDOR_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.DEATH_STAR_II_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.TATOOINE_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.CORUSCANT_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.THEED_PALACE_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.REFLECTIONS_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.REFLECTIONS_II_BOOSTER_PACK, 1);
        secondWeek.addItem(ProductName.REFLECTIONS_III_BOOSTER_PACK, 1);

        allofthejediSealed.add(secondWeek);

        List<PoolIntPair> secondWeekPools = new LinkedList<>();
        //nothing
        pools.add(secondWeekPools);

        _fixedFromPool.put(SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getSealedCode(), pools);

        _collections.put(SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getSealedCode(), allofthejediSealed);
    }

    /**
     * Creates the collection of products to use for novelty sealed
     */
    private void createNoveltySealed() {

        //current novelty sealed:
        // Enhanced Choice Sealed
        List<CardCollection> enhancedChoiceSealed = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection();
        firstWeek.addItem("(S)Enhanced Premiere Pack Choice -- Dark", 1);
        firstWeek.addItem("(S)Enhanced Premiere Pack Choice -- Light", 1);
        firstWeek.addItem("(S)Enhanced Cloud City Pack Choice", 2);
        firstWeek.addItem("(S)Enhanced Jabba's Palace Pack Choice", 2);
        enhancedChoiceSealed.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection();
        secondWeek.addItem("(S)Enhanced Cloud City Pack Choice", 1);
        secondWeek.addItem("(S)Enhanced Jabba's Palace Pack Choice", 1);
        enhancedChoiceSealed.add(secondWeek);

        _collections.put(SealedLeagueType.NOVELTY_SEALED.getSealedCode(), enhancedChoiceSealed);
    }

    private void createLightspeedSealed() {
        //  Lightspeed Sealed (1 series)
        //  series 1 = randomly select two "packets" of 20 cards

        List<CardCollection> noveltySealed = new ArrayList<>();
        List<List<PoolIntPair>> pools = new LinkedList<>();


        MutableCardCollection firstWeek = new DefaultCardCollection();
        //first week
        //nothing here

        noveltySealed.add(firstWeek);

        List<String> darkPacketPool = new LinkedList<>();
        List<String> lightPacketPool = new LinkedList<>();

        darkPacketPool.add("LIGHTSPEED_DS_BIKER_SCOUTS");
        darkPacketPool.add("LIGHTSPEED_DS_BOUNTY_HUNTERS");
        darkPacketPool.add("LIGHTSPEED_DS_CRIMSON_DAWN");
        darkPacketPool.add("LIGHTSPEED_DS_DARK_JEDI");
        darkPacketPool.add("LIGHTSPEED_DS_DROIDS");
        darkPacketPool.add("LIGHTSPEED_DS_FIRST_ORDER");
        darkPacketPool.add("LIGHTSPEED_DS_INQUISITORS");
        darkPacketPool.add("LIGHTSPEED_DS_JABBAS_PALACE");
        darkPacketPool.add("LIGHTSPEED_DS_SEPARATISTS");
        darkPacketPool.add("LIGHTSPEED_DS_STAR_DESTROYERS");
        darkPacketPool.add("LIGHTSPEED_DS_TIES");
        darkPacketPool.add("LIGHTSPEED_DS_WALKERS");
        darkPacketPool.add("LIGHTSPEED_DS_XIZORS_PALACE");
        lightPacketPool.add("LIGHTSPEED_LS_CLONES");
        lightPacketPool.add("LIGHTSPEED_LS_GHOST_CREW");
        lightPacketPool.add("LIGHTSPEED_LS_GUNGANS");
        lightPacketPool.add("LIGHTSPEED_LS_JEDI");
        lightPacketPool.add("LIGHTSPEED_LS_MAZS_CASTLE");
        lightPacketPool.add("LIGHTSPEED_LS_RESISTANCE");
        lightPacketPool.add("LIGHTSPEED_LS_SCOUNDRELS");
        lightPacketPool.add("LIGHTSPEED_LS_SCOUTS");
        lightPacketPool.add("LIGHTSPEED_LS_SKYWALKERS");
        lightPacketPool.add("LIGHTSPEED_LS_STAR_CRUISERS");
        lightPacketPool.add("LIGHTSPEED_LS_STARFIGHTERS");
        lightPacketPool.add("LIGHTSPEED_LS_WOOKIEES");
        lightPacketPool.add("LIGHTSPEED_LS_JAWAS");

        List<PoolIntPair> firstWeekPools = new LinkedList<>();
        firstWeekPools.add(new PoolIntPair(darkPacketPool, 2));
        firstWeekPools.add(new PoolIntPair(lightPacketPool, 2));

        pools.add(firstWeekPools);

        _fixedFromPool.put(SealedLeagueType.NOVELTY_SEALED.getSealedCode(), pools);

        _collections.put(SealedLeagueType.NOVELTY_SEALED.getSealedCode(), noveltySealed);
    }

    /*
     * Adds the cards from the specified packet to the specified collection
     * @param collection the collection
     * @param packet the packet code
     */
    private void addLightspeedSealedPacket(MutableCardCollection collection, String packet) {
        switch(packet) {
            case "LIGHTSPEED_DS_BIKER_SCOUTS":
                collection.addItem("8_97",1); //Corporal Drelosyn
                collection.addItem("8_99",1); //Corporal Oberk
                collection.addItem("208_30",1); //Darth Vader, Emperor's Enforcer
                collection.addItem("212_2",1); //Moff Gideon
                collection.addItem("8_111",1); //Sergeant Barich
                collection.addItem("8_112",1); //Sergeant Elsek
                collection.addItem("8_113",1); //Sergeant Irol
                collection.addItem("200_103",1); //Aratech Corporation (V)
                collection.addItem("8_126",1); //Imperial Arrest Order
                collection.addItem("8_134",1); //Accelerate
                collection.addItem("8_145",1); //High-speed Tactics
                collection.addItem("205_20",1); //Point Man (V)
                collection.addItem("5_159",1); //Trooper Assault
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("8_161",1); //Endor: Dark Forest
                collection.addItem("8_164",1); //Endor: Forest Clearing
                collection.addItem("8_166",1); //Endor: Landing Platform (Docking Bay)
                collection.addItem("214_5",1); //Intimidator & Persecutor
                collection.addItem("8_169",2); //Speeder Bike
                break;
            case "LIGHTSPEED_DS_BOUNTY_HUNTERS":
                collection.addItem("212_3",1); //Aurra Sing With Blaster Rifle
                collection.addItem("6_95",1); //Bane Malar
                collection.addItem("206_9",1); //Boba Fett (V)
                collection.addItem("203_24",1); //Cad Bane
                collection.addItem("200_79",1); //Dengar With Blaster Carbine (V)
                collection.addItem("201_25^",1); //Jango Fett
                collection.addItem("204_46",1); //Zam Wesell
                collection.addItem("109_11",1); //IG-88 With Riot Gun
                collection.addItem("6_141",1); //All Wrapped Up
                collection.addItem("208_43",1); //Double Back (V)
                collection.addItem("6_154",1); //Hidden Weapons
                collection.addItem("7_255",1); //Jabba's Through With You
                collection.addItem("4_146",1); //Res Luk Ra'auf
                collection.addItem("208_45",1); //Sonic Bombardment (V)
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("6_164",1); //Jabba's Palace: Dungeon
                collection.addItem("1_289",1); //Tatooine
                collection.addItem("6_171",1); //Tatooine: Jabba's Palace
                collection.addItem("201_40^",1); //Slave I, Symbol Of Fear
                collection.addItem("110_12",1); //Zuckuss In Mist Hunter
                break;
            case "LIGHTSPEED_DS_CRIMSON_DAWN":
                collection.addItem("213_2",1); //Aemon Gremm With Percussive Cannon
                collection.addItem("213_4",1); //Dryden Vos
                collection.addItem("213_7",1); //Hylobon Enforcer
                collection.addItem("213_9",1); //Margo
                collection.addItem("217_19",1); //Qi'ra, Top Lieutenant
                collection.addItem("207_22",1); //Quiggold
                collection.addItem("207_24",1); //Sidon Ithano
                collection.addItem("9_126",1); //Inconsequential Losses
                collection.addItem("200_114",1); //You'll Be Dead!
                collection.addItem("213_17",1); //A Lawless Time
                collection.addItem("213_19",1); //I Never Ask For Anything Twice
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("213_22",1); //Working Much More Closely
                collection.addItem("213_25",1); //First Light: Bar
                collection.addItem("213_26",1); //First Light: Dryden's Study
                collection.addItem("213_27",1); //First Light: Reception Area
                collection.addItem("200_130",1); //Boba Fett In Slave I (V)
                collection.addItem("207_28",1); //Meson Martinet
                collection.addItem("213_34",2); //Crimson Dawn Blaster
                break;
            case "LIGHTSPEED_DS_DARK_JEDI":
                collection.addItem("200_76",1); //Count Dooku
                collection.addItem("9_109",1); //Emperor Palpatine
                collection.addItem("205_12",1); //Emperor Palpatine, Foreseer
                collection.addItem("200_90",1); //Sith Probe Droid (V)
                collection.addItem("209_37^",1); //Kylo Ren With Lightsaber
                collection.addItem("216_6",2); //Darth Vader, Betrayer Of The Jedi
                collection.addItem("208_34",1); //Lord Maul With Lightsaber
                collection.addItem("205_17",1); //I Am Your Father (V)
                collection.addItem("211_12",1); //Always Two There Are
                collection.addItem("9_136",1); //Force Lightning
                collection.addItem("1_248",1); //I Have You Now
                collection.addItem("12_154",1); //Neimoidian Advisor
                collection.addItem("12_157",1); //Sense
                collection.addItem("200_123",1); //Sith Fury (V)
                collection.addItem("5_166",1); //Cloud City: Carbonite Chamber
                collection.addItem("5_167",1); //Cloud City: Chasm Walkway
                collection.addItem("209_50^",1); //Mustafar: Vader's Castle
                collection.addItem("9_156",1); //Emperor's Personal Shuttle
                collection.addItem("1_324",1); //Vader's Lightsaber
                break;
            case "LIGHTSPEED_DS_DROIDS":
                collection.addItem("209_35^",1); //Dr. Chelli Lona Aphra
                collection.addItem("14_70",1); //3B3-1204
                collection.addItem("204_36",2); //B2 Battle Droid
                collection.addItem("14_73",1); //Battle Droid Officer
                collection.addItem("14_80",1); //Infantry Battle Droid
                collection.addItem("14_84",1); //OWO-1 With Backup
                collection.addItem("12_114",1); //P-59
                collection.addItem("12_103",1); //Daultay Dofine
                collection.addItem("14_96",1); //Droid Racks
                collection.addItem("210_45",1); //Silence Is Golden (V)
                collection.addItem("14_103",1); //Halt!
                collection.addItem("12_154",1); //Neimoidian Advisor
                collection.addItem("3_134",1); //Self-Destruct Mechanism
                collection.addItem("14_107",1); //There They Are!
                collection.addItem("12_163",1); //We Must Accelerate Our Plans
                collection.addItem("12_164",1); //Blockade Flagship: Bridge
                collection.addItem("5_170",1); //Cloud City: Incinerator
                collection.addItem("6_163",1); //Jabba's Palace: Droid Workshop
                collection.addItem("14_114",1); //Blockade Flagship
                break;
            case "LIGHTSPEED_DS_FIRST_ORDER":
                collection.addItem("209_39^",1); //Supreme Leader Snoke
                collection.addItem("208_37",1); //Captain Peavey
                collection.addItem("204_38^",1); //Captain Phasma
                collection.addItem("204_39",1); //FN-2003
                collection.addItem("208_31",1); //FN-2199 (Nines)
                collection.addItem("204_40",1); //First Order Stormtrooper
                collection.addItem("204_41",1); //General Hux
                collection.addItem("204_43",2); //Kylo Ren
                collection.addItem("204_47",1); //Bow To The First Order
                collection.addItem("204_49",1); //Force Freeze
                collection.addItem("213_21",1); //Imperial Code Cylinder (V)
                collection.addItem("200_123",1); //Sith Fury (V)
                collection.addItem("5_159",1); //Trooper Assault
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("208_51^",1); //Starkiller Base
                collection.addItem("208_52",1); //Starkiller Base: Docking Bay
                collection.addItem("208_55",1); //Starkiller Base: Shield Control
                collection.addItem("204_54^",1); //Finalizer
                collection.addItem("204_57",1); //F-11D Blaster Rifle
                break;
            case "LIGHTSPEED_DS_INQUISITORS":
                collection.addItem("213_8",1); //ID9 Probe Droid
                collection.addItem("108_6",1); //Darth Vader With Lightsaber
                collection.addItem("213_5",1); //Eighth Brother
                collection.addItem("213_6",1); //Fifth Brother
                collection.addItem("213_11",1); //Ninth Sister
                collection.addItem("213_12",1); //Seventh Sister
                collection.addItem("210_46",1); //The Grand Inquisitor
                collection.addItem("210_31",1); //The Dark Path (V)
                collection.addItem("213_15",1); //There Are Many Hunting You Now
                collection.addItem("213_18",2); //Far More Frightening Than Death
                collection.addItem("200_119",1); //Force Field (V)
                collection.addItem("213_20",1); //I've Been Searching For You For Some Time
                collection.addItem("10_47",1); //Sniper & Dark Strike
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("200_126",1); //Cloud City: Security Tower (V)
                collection.addItem("213_29",1); //Malachor: Sith Temple Entrance
                collection.addItem("213_28",1); //Malachor: Sith Temple Upper Chamber
                collection.addItem("208_58",1); //Mara Jade In VT-49 Decimator
                collection.addItem("211_25",1); //Dark Jedi Lightsaber (V)
                break;
            case "LIGHTSPEED_DS_JABBAS_PALACE":
                collection.addItem("209_33",1); //Bala-Tik
                collection.addItem("6_104",1); //Gailid
                collection.addItem("200_84",1); //Jabba The Hutt (V)
                collection.addItem("211_3",1); //Lady Proxima
                collection.addItem("211_1",1); //Mitth'raw'nuruodo
                collection.addItem("203_28",1); //Ortugg (V)
                collection.addItem("200_87",1); //Ponda Baba (V)
                collection.addItem("201_27",1); //Wooof (V)
                collection.addItem("109_6",1); //4-LOM With Concussion Rifle
                collection.addItem("201_31",1); //Jabba's Haven
                collection.addItem("202_12",1); //Jabba's Trophies
                collection.addItem("7_256",1); //Jabba's Twerps
                collection.addItem("6_157",1); //None Shall Pass
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("211_15",1); //Twi'lek Advisor (V)
                collection.addItem("6_162",1); //Jabba's Palace: Audience Chamber
                collection.addItem("112_12",1); //Jabba's Palace: Lower Passages
                collection.addItem("6_168",1); //Nal Hutta
                collection.addItem("200_134",1); //Elis In Hinthra
                collection.addItem("110_12",1); //Zuckuss In Mist Hunter
                break;
            case "LIGHTSPEED_DS_SEPARATISTS":
                collection.addItem("14_78",1); //Darth Sidious
                collection.addItem("213_3",1); //Darth Tyranus
                collection.addItem("212_5",1); //Admiral Trench
                collection.addItem("203_27^",1); //General Grievous
                collection.addItem("210_40",1); //Nute Gunray
                collection.addItem("301_3^",1); //Asajj Ventress With Lightsabers
                collection.addItem("12_130",1); //Begin Landing Your Troops
                collection.addItem("12_143",1); //Wipe Them Out, All Of Them
                collection.addItem("200_120",1); //Force Push (V)
                collection.addItem("9_137",2); //Imperial Command
                collection.addItem("12_154",1); //Neimoidian Advisor
                collection.addItem("10_47",1); //Sniper & Dark Strike
                collection.addItem("211_14",1); //Trade Federation Tactics (V)
                collection.addItem("12_165",1); //Coruscant
                collection.addItem("211_20",1); //Invisible Hand: Bridge
                collection.addItem("211_22",1); //Invisible Hand: Hallway 328
                collection.addItem("200_129",1); //Blockade Support Ship
                collection.addItem("211_23",1); //Invisible Hand
                collection.addItem("211_25",1); //Dark Jedi Lightsaber (V)
                break;
            case "LIGHTSPEED_DS_STAR_DESTROYERS":
                collection.addItem("9_97",1); //Admiral Chiraneau
                collection.addItem("3_82",1); //Admiral Ozzel
                collection.addItem("10_40",1); //Grand Admiral Thrawn
                collection.addItem("209_36",1); //Krennic, Death Star Commandant
                collection.addItem("200_111",1); //Kuat Drive Yards (V)
                collection.addItem("200_113",1); //Tarkin Doctrine (V)
                collection.addItem("209_46",1); //Apology Accepted (V)
                collection.addItem("209_47",1); //Death Squadron Assignment
                collection.addItem("9_137",1); //Imperial Command
                collection.addItem("201_36",2); //TIE Sentry Ships (V)
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("2_143",1); //Death Star
                collection.addItem("4_164",1); //Raithal
                collection.addItem("7_287",1); //Rendili
                collection.addItem("9_154",1); //Chimaera
                collection.addItem("216_8",1); //Devastator (V)
                collection.addItem("201_41",1); //Stalker (V)
                collection.addItem("3_153",1); //Tyrant
                collection.addItem("211_24",1); //Vengeance (V)
                break;
            case "LIGHTSPEED_DS_TIES":
                collection.addItem("210_30",1); //Commander Brandei (V)
                collection.addItem("9_129",1); //Mobilization Points
                collection.addItem("7_241",1); //Sienar Fleet Systems
                collection.addItem("7_246",1); //All Power To Weapons
                collection.addItem("1_241",1); //Dark Maneuvers
                collection.addItem("7_262",1); //Short Range Fighters
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("8_156",1); //Carida
                collection.addItem("7_286",1); //Kuat
                collection.addItem("213_30",1); //Wakeelmui (V)
                collection.addItem("9_153",1); //Black 11
                collection.addItem("106_10",3); //Black Squadron TIE
                collection.addItem("205_21",1); //Captain Jonus In Scimitar 2
                collection.addItem("200_132",1); //Colonel Jendon In Onyx 1
                collection.addItem("105_5",1); //Death Star Assault Squadron
                collection.addItem("106_13",2); //Dreadnaught-Class Heavy Cruiser
                collection.addItem("200_136",1); //Onyx 2 (V)
                break;
            case "LIGHTSPEED_DS_WALKERS":
                collection.addItem("8_94",1); //Commander Igar
                collection.addItem("208_30",1); //Darth Vader, Emperor's Enforcer
                collection.addItem("202_10",1); //General Nevar
                collection.addItem("200_81",1); //General Veers (V)
                collection.addItem("200_107",1); //Image Of The Dark Lord (V)
                collection.addItem("11_77",1); //You May Start Your Landing
                collection.addItem("209_47",1); //Death Squadron Assignment
                collection.addItem("9_137",1); //Imperial Command
                collection.addItem("3_138",1); //Trample
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("104_7",1); //Walker Garrison
                collection.addItem("3_144",1); //Hoth: Defensive Perimeter (3rd Marker)
                collection.addItem("3_147",1); //Hoth: Echo Docking Bay
                collection.addItem("208_49",1); //Hoth: Ice Plains (5th Marker) (V)
                collection.addItem("200_139",1); //Blizzard 2 (V)
                collection.addItem("215_23",1); //Blizzard 8
                collection.addItem("200_140",1); //Blizzard Scout 1 (V)
                collection.addItem("104_5",1); //Imperial Walker
                collection.addItem("210_38",1); //Marquand In Blizzard 6
                collection.addItem("8_170",1); //Tempest 1
                break;
            case "LIGHTSPEED_DS_XIZORS_PALACE":
                collection.addItem("200_74",1); //Arica (V)
                collection.addItem("212_3",1); //Aurra Sing With Blaster Rifle
                collection.addItem("203_23",1); //Baniss Keeg, Pilot Instructor
                collection.addItem("200_75",1); //Bossk (V)
                collection.addItem("203_24",1); //Cad Bane
                collection.addItem("10_45",1); //Prince Xizor
                collection.addItem("200_91",1); //Vigo (V)
                collection.addItem("10_41",1); //Guri
                collection.addItem("211_10",1); //Quietly Observing (V)
                collection.addItem("206_12",1); //Xizor's Bounty
                collection.addItem("201_33",1); //A Dark Time For The Rebellion (V)
                collection.addItem("10_39",1); //Ghhhk & Those Rebels Won't Escape Us
                collection.addItem("1_254",1); //Kintan Strider
                collection.addItem("6_160",1); //Twi'lek Advisor
                collection.addItem("4_153",1); //Voyeur
                collection.addItem("203_31",1); //Coruscant (V)
                collection.addItem("203_32",1); //Coruscant: Xizor's Palace
                collection.addItem("209_51",1); //Xizor's Palace: Sewer
                collection.addItem("203_34",1); //Xizor's Palace: Uplink Station
                collection.addItem("203_35",1); //Falleen's Fist
                break;
            case "LIGHTSPEED_LS_CLONES":
                collection.addItem("208_3",1); //CC-2237 (Odd Ball)
                collection.addItem("209_3",1); //CT-5385 (Tup)
                collection.addItem("203_2^",1); //CT-5555 (Fives)
                collection.addItem("200_3^",1); //Captain Rex, 501st Legion
                collection.addItem("210_8",1); //Clone Squad Leader
                collection.addItem("210_9",2); //Clone Trooper
                collection.addItem("200_6^",1); //Commander Cody
                collection.addItem("209_6^",1); //General Kenobi
                collection.addItem("211_53",1); //Cloning Cylinders
                collection.addItem("217_28",1); //Another Pathetic Lifeform & Security Control
                collection.addItem("210_2",1); //Ambush (V)
                collection.addItem("215_10",2); //For the Republic!
                collection.addItem("12_62",1); //Mindful Of The Future
                collection.addItem("211_43",1); //Kamino
                collection.addItem("211_42",1); //Kamino: Clone Birthing Center
                collection.addItem("211_41",1); //Kamino: Clone Training Center
                collection.addItem("200_59",2); //Acclamator-Class Assault Ship
                break;
            case "LIGHTSPEED_LS_GHOST_CREW":
                collection.addItem("208_2",1); //C1-10P (Chopper)
                collection.addItem("211_59",1); //Ahsoka Tano
                collection.addItem("204_3^",1); //Captain Hera Syndulla
                collection.addItem("210_14",1); //Ezra Bridger
                collection.addItem("203_6^",1); //Kanan Jarrus
                collection.addItem("207_9",1); //Sabine Wren
                collection.addItem("208_13",1); //Zeb Orrelios
                collection.addItem("12_44",1); //Insurrection & Aim High
                collection.addItem("1_70",1); //A Few Maneuvers
                collection.addItem("208_20",1); //Mandalorian Mishap (V)
                collection.addItem("1_105",1); //Rebel Barrier
                collection.addItem("6_77",1); //The Signal
                collection.addItem("207_15",2); //This Is MY Ship!
                collection.addItem("106_2",1); //Corulag
                collection.addItem("217_44",1); //Profundity: Docking Bay
                collection.addItem("1_129",1); //Tatooine: Docking Bay 94
                collection.addItem("207_17",1); //Ghost
                collection.addItem("208_27",1); //Phantom
                collection.addItem("214_15",1); //Ahsoka's Shoto Lightsaber
                break;
            case "LIGHTSPEED_LS_GUNGANS":
                collection.addItem("14_5",1); //Boss Nass
                collection.addItem("14_7",1); //Captain Tarpals
                collection.addItem("14_11",1); //General Jar Jar
                collection.addItem("14_12",2); //Gungan General
                collection.addItem("14_28",1); //Rep Been
                collection.addItem("14_32",1); //Gungan Energy Shield
                collection.addItem("14_34",1); //Naboo Celebration
                collection.addItem("14_41",1); //Big Boomers!
                collection.addItem("12_62",1); //Mindful Of The Future
                collection.addItem("203_17",1); //Rebel Leadership (V)
                collection.addItem("14_46",2); //Wesa Gotta Grand Army
                collection.addItem("12_79",1); //Naboo: Battle Plains
                collection.addItem("14_49",1); //Naboo: Boss Nass' Chambers
                collection.addItem("14_50",1); //Naboo: Otoh Gunga Entrance
                collection.addItem("205_9",1); //Ric In Queen's Royal Starship
                collection.addItem("14_59",2); //Fambaa
                collection.addItem("14_63",1); //Booma
                break;
            case "LIGHTSPEED_LS_JAWAS":
                collection.addItem("6_5",1); //Aved Luun
                collection.addItem("12_9",2); //Jawa
                collection.addItem("6_22",1); //Kalit
                collection.addItem("216_40",2); //Offworld Jawas
                collection.addItem("208_10",1); //R'kik D'nec, Hero Of The Dune Sea (V)
                collection.addItem("6_53",1); //Bargaining Table
                collection.addItem("12_41",1); //Battle Plan & Draw Their Fire
                collection.addItem("6_65",2); //Dune Sea Sabacc
                collection.addItem("10_8",1); //Houjix & Out Of Nowhere
                collection.addItem("10_13",1); //Nar Shaddaa Wind Chimes & Out Of Somewhere
                collection.addItem("6_77",1); //The Signal
                collection.addItem("6_86",1); //Tatooine: Hutt Canyon
                collection.addItem("112_9",1); //Tatooine: Hutt Trade Route (Desert)
                collection.addItem("1_131",1); //Tatooine: Jawa Camp
                collection.addItem("205_7",1); //Han, Chewie, And The Falcon (V)
                collection.addItem("7_155",2); //Ronto
                break;
            case "LIGHTSPEED_LS_JEDI":
                collection.addItem("210_19^",1); //Kit Fisto
                collection.addItem("201_2",1); //Mace Windu (V)
                collection.addItem("210_23",1); //Plo Koon (V)
                collection.addItem("213_44",1); //Yoda, Master Of The Force (V)
                collection.addItem("301_1^",1); //Ahsoka Tano With Lightsabers
                collection.addItem("216_21",1); //Anakin Skywalker, Jedi Knight
                collection.addItem("209_6^",1); //General Kenobi
                collection.addItem("12_39",1); //Another Pathetic Lifeform
                collection.addItem("12_55",1); //Are You Brain Dead?!
                collection.addItem("209_20",1); //Knights Of The Old Republic
                collection.addItem("12_62",1); //Mindful Of The Future
                collection.addItem("12_68",1); //Sense
                collection.addItem("10_23",1); //Sorry About The Mess & Blaster Proficiency
                collection.addItem("12_69",1); //Speak With The Jedi Council
                collection.addItem("210_1^",1); //Ahch-To: Saddle
                collection.addItem("12_76",1); //Coruscant: Jedi Council Chamber
                collection.addItem("200_57^",1); //Coruscant: Night Club
                collection.addItem("214_14",1); //ARC-170 Starfighter
                collection.addItem("211_33",2); //Jedi Lightsaber (V)
                break;
            case "LIGHTSPEED_LS_MAZS_CASTLE":
                collection.addItem("200_1^",1); //Aayla Secura
                collection.addItem("204_4",1); //Chewie With Bowcaster
                collection.addItem("214_18",1); //Din Djarin
                collection.addItem("203_4",1); //Ellorrs Madak, Pilot Instructor
                collection.addItem("216_27",1); //Figrin D'an & The Modal Nodes
                collection.addItem("211_57",1); //Maz Kanata
                collection.addItem("7_32",1); //Melas
                collection.addItem("10_12",1); //Mirax Terrik
                collection.addItem("209_14",1); //Yoxgit (V)
                collection.addItem("210_7",1); //Ancient Watering Hole
                collection.addItem("6_62",1); //Choke
                collection.addItem("200_54",1); //Jedi Levitation (V)
                collection.addItem("7_94",1); //Local Defense
                collection.addItem("6_77",1); //The Signal
                collection.addItem("211_40",1); //Maz's Castle: Antechamber
                collection.addItem("211_39",1); //Maz's Castle: Hidden Recess
                collection.addItem("211_37",1); //Takodana
                collection.addItem("211_38",1); //Takodana: Maz's Castle
                collection.addItem("200_60",1); //Booster In Pulsar Skate
                collection.addItem("203_21^",1); //Wild Karrde
                break;
            case "LIGHTSPEED_LS_RESISTANCE":
                collection.addItem("204_6",1); //Finn
                collection.addItem("207_5",1); //General Leia Organa
                collection.addItem("215_12",1); //Jannah
                collection.addItem("204_7",1); //Lor San Tekka
                collection.addItem("204_8",1); //Poe Dameron
                collection.addItem("209_10^",1); //Rey With Lightsaber
                collection.addItem("209_11",1); //Rose Tico
                collection.addItem("211_55",1); //Vice Admiral Holdo
                collection.addItem("208_15",1); //Why Does Everyone Want To Go Back To Jakku?!
                collection.addItem("204_17",1); //Are You Okay?
                collection.addItem("207_13",1); //I Think I Can Handle Myself
                collection.addItem("211_2",1); //See You Around, Kid
                collection.addItem("6_77",1); //The Signal
                collection.addItem("207_14",1); //They're Tracking Us (V)
                collection.addItem("207_16",1); //Jakku: Docking Bay
                collection.addItem("204_29",1); //Jakku: Rey's Encampment
                collection.addItem("204_31",1); //Jakku: Tuanul Village
                collection.addItem("211_28",1); //BB-8 In Black Squadron 1
                collection.addItem("213_58",1); //Leia's Resistance Transport
                collection.addItem("210_26",1); //V-4X-D Ski Speeder
                break;
            case "LIGHTSPEED_LS_SCOUNDRELS":
                collection.addItem("213_35",1); //Captain Lando Calrissian
                collection.addItem("213_36",1); //Chewbacca (V)
                collection.addItem("213_37",1); //Han... Solo
                collection.addItem("213_39",1); //Qi'ra
                collection.addItem("213_41",1); //Rio Durant
                collection.addItem("213_43",1); //Val
                collection.addItem("10_9",1); //LE-BO2D9 (Leebo)
                collection.addItem("213_46",1); //Kessel Run (V)
                collection.addItem("208_16",1); //Yarna d'al' Gargan (V)
                collection.addItem("12_53",1); //All Wings Report In & Darklighter Spin
                collection.addItem("213_50",1); //Han's Dice (V)
                collection.addItem("213_51",1); //He's The Best Smuggler Around
                collection.addItem("213_53",1); //I've Got A Really Good Feeling About This
                collection.addItem("5_64",1); //Punch It!
                collection.addItem("6_77",1); //The Signal
                collection.addItem("1_126",1); //Kessel
                collection.addItem("1_127",1); //Tatooine
                collection.addItem("1_133",1); //Tatooine: Mos Eisley
                collection.addItem("1_143",1); //Millennium Falcon
                collection.addItem("203_21^",1); //Wild Karrde
                break;
            case "LIGHTSPEED_LS_SCOUTS":
                collection.addItem("8_2",1); //Chewbacca Of Kashyyyk
                collection.addItem("8_7",1); //Corporal Kensaric
                collection.addItem("200_9",1); //Daughter Of Skywalker (V)
                collection.addItem("200_12",1); //General Airen Cracken
                collection.addItem("8_14",1); //General Crix Madine
                collection.addItem("215_11",1); //Han Solo, Optimistic General
                collection.addItem("9_21",1); //Lieutenant Blount
                collection.addItem("200_21",1); //Luke Skywalker, Rebel Scout (V)
                collection.addItem("8_28",1); //Sergeant Bruckman
                collection.addItem("9_41",1); //Strike Planning
                collection.addItem("203_15",1); //The Shield Is Down! (V)
                collection.addItem("9_52",1); //Insertion Planning
                collection.addItem("203_17",1); //Rebel Leadership (V)
                collection.addItem("8_61",1); //Take the Initiative
                collection.addItem("6_77",1); //The Signal
                collection.addItem("204_24",1); //Endor (V)
                collection.addItem("8_69",1); //Endor: Back Door
                collection.addItem("8_77",1); //Endor: Rebel Landing Site (Forest)
                collection.addItem("200_62",1); //Gold Leader In Gold 1 (V)
                collection.addItem("201_19",1); //Tantive IV (V)
                break;
            case "LIGHTSPEED_LS_SKYWALKERS":
                collection.addItem("11_12",1); //Shmi Skywalker
                collection.addItem("11_13",1); //Threepio With His Parts Showing
                collection.addItem("200_9",1); //Daughter Of Skywalker (V)
                collection.addItem("108_3",1); //Luke With Lightsaber
                collection.addItem("200_2^",2); //Anakin Skywalker, Padawan Learner
                collection.addItem("203_36",1); //Padme Naberrie (V)
                collection.addItem("217_32",1); //Cliegg Lars
                collection.addItem("200_41",1); //I Must Be Allowed To Speak (V)
                collection.addItem("6_56",1); //Projection Of A Skywalker
                collection.addItem("208_14",1); //Prophecy Of The Force
                collection.addItem("6_61",1); //Blaster Deflection
                collection.addItem("1_110",1); //Skywalkers
                collection.addItem("203_18",1); //The Force Is Strong With This One (V)
                collection.addItem("6_77",1); //The Signal
                collection.addItem("208_23",1); //Endor: Ewok Village (V)
                collection.addItem("205_6",1); //Tatooine: Lars' Moisture Farm (V)
                collection.addItem("217_47",1); //Tatooine: Skywalker Hut
                collection.addItem("201_19",1); //Tantive IV (V)
                collection.addItem("3_71",1); //Anakin's Lightsaber
                break;
            case "LIGHTSPEED_LS_STAR_CRUISERS":
                collection.addItem("203_1",1); //Admiral Ackbar (V)
                collection.addItem("209_1",1); //Admiral Raddus
                collection.addItem("9_12",1); //First Officer Thaneespi
                collection.addItem("203_9",1); //Mon Calamari Admiral
                collection.addItem("217_26",1); //Admiral Kilian
                collection.addItem("9_36",1); //Launching The Assault
                collection.addItem("200_44",1); //Mon Calamari Dockyards
                collection.addItem("207_11",1); //Rebellions Are Built On Hope
                collection.addItem("203_17",1); //Rebel Leadership (V)
                collection.addItem("200_56",1); //Starship Levitation (V)
                collection.addItem("6_77",1); //The Signal
                collection.addItem("12_72",1); //We Wish To Board At Once
                collection.addItem("8_67",1); //Chandrila
                collection.addItem("9_58",1); //Home One: War Room
                collection.addItem("9_59",1); //Mon Calamari
                collection.addItem("203_20",1); //Bright Hope (V)
                collection.addItem("9_74",1); //Home One
                collection.addItem("9_75",1); //Independence
                collection.addItem("9_76",1); //Liberty
                collection.addItem("207_18^",1); //Profundity
                break;
            case "LIGHTSPEED_LS_STARFIGHTERS":
                collection.addItem("10_6",1); //Dash Rendar
                collection.addItem("204_11",1); //Solo
                collection.addItem("208_8",1); //Luke Skywalker, The Rebellion's Hope
                collection.addItem("9_31",1); //Wedge Antilles, Red Squadron Leader
                collection.addItem("204_9^",1); //Rey
                collection.addItem("5_24",1); //Haven
                collection.addItem("9_39",1); //Squadron Assignments
                collection.addItem("1_70",1); //A Few Maneuvers
                collection.addItem("12_53",1); //All Wings Report In & Darklighter Spin
                collection.addItem("10_4",1); //Control & Tunnel Vision
                collection.addItem("208_18",1); //Corellian Slip (V)
                collection.addItem("6_77",1); //The Signal
                collection.addItem("200_58^",1); //Nar Shaddaa
                collection.addItem("9_60",1); //Sullust
                collection.addItem("211_32",1); //Yavin 4 (V)
                collection.addItem("111_2",1); //Artoo-Detoo In Red 5
                collection.addItem("10_17",1); //Outrider
                collection.addItem("9_81",1); //Red Squadron 1
                collection.addItem("206_7^",1); //Rogue One
                collection.addItem("206_8",1); //The Falcon
                break;
            case "LIGHTSPEED_LS_WOOKIEES":
                collection.addItem("216_23",1); //Chewbacca, Defender Of Kashyyyk
                collection.addItem("217_36", 1); //Grakchawwaa
                collection.addItem("216_42",1); //Tarfful
                collection.addItem("7_50",1); //Wookiee
                collection.addItem("216_48",2); //Wookiee Warrior
                collection.addItem("202_4",1); //Yoda, Keeper Of The Peace
                collection.addItem("216_49",1); //Yarua (V)
                collection.addItem("6_53",1); //Bargaining Table
                collection.addItem("216_46",1); //Wookiee Homestead
                collection.addItem("12_62",1); //Mindful Of The Future
                collection.addItem("10_13",1); //Nar Shaddaa Wind Chimes & Out Of Somewhere
                collection.addItem("216_45",1); //Wookiee Guide (V)
                collection.addItem("216_47",1); //Wookiee Roar (V)
                collection.addItem("5_75",1); //Wookiee Strangle
                collection.addItem("215_14",1); //Kashyyyk: Kachirho
                collection.addItem("216_32",1); //Kashyyyk: Sacred Falls (Forest)
                collection.addItem("216_33",1); //Kashyyyk: Work Settlement #121
                collection.addItem("200_59",1); //Acclamator-Class Assault Ship
                collection.addItem("216_22",1); //Bowcaster (V)
                break;
            default:
                break;
        }
    }

    /**
     * Creates the collection of products to use for Watto's Cube with Objective Packs
     */
    private void createWattosCubeWithObjective() {
        List<CardCollection> cube = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection(true);
        firstWeek.addItem(ProductName.CUBE_OBJECTIVE_PACK_DARK, 2);
        firstWeek.addItem(ProductName.CUBE_DRAFT_PACK_DARK, 7);
        firstWeek.addItem(ProductName.CUBE_OBJECTIVE_PACK_LIGHT, 2);
        firstWeek.addItem(ProductName.CUBE_DRAFT_PACK_LIGHT, 7);
        cube.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection(true);
        secondWeek.addItem(ProductName.CUBE_OBJECTIVE_PACK_DARK, 1);
        secondWeek.addItem(ProductName.CUBE_DRAFT_PACK_DARK, 2);
        secondWeek.addItem(ProductName.CUBE_OBJECTIVE_PACK_LIGHT, 1);
        secondWeek.addItem(ProductName.CUBE_DRAFT_PACK_LIGHT, 2);
        cube.add(secondWeek);

        _collections.put(SealedLeagueType.WATTOS_CUBE_WITH_OBJECTIVE_PACKS.getSealedCode(), cube);
    }

    /**
     * Creates the collection of products to use for Watto's Cube with Objective Packs
     */
    private void createWattosCubeWithFixed() {
        List<CardCollection> cube = new ArrayList<CardCollection>();

        MutableCardCollection firstWeek = new DefaultCardCollection(true);
        firstWeek.addItem(ProductName.CUBE_FIXED_PACK_DARK, 1);
        firstWeek.addItem(ProductName.CUBE_DRAFT_PACK_DARK, 8);
        firstWeek.addItem(ProductName.CUBE_FIXED_PACK_LIGHT, 1);
        firstWeek.addItem(ProductName.CUBE_DRAFT_PACK_LIGHT, 8);
        cube.add(firstWeek);

        MutableCardCollection secondWeek = new DefaultCardCollection(true);
        secondWeek.addItem(ProductName.CUBE_DRAFT_PACK_DARK, 3);
        secondWeek.addItem(ProductName.CUBE_DRAFT_PACK_LIGHT, 3);
        cube.add(secondWeek);

        _collections.put(SealedLeagueType.WATTOS_CUBE_WITH_FIXED.getSealedCode(), cube);
    }

    /**
     * Gets the number of series in the specified league.
     * @param leagueCode the league code
     * @return the collection
     */
    public int getNumSeriesInLeague(String leagueCode) {
        return _collections.get(leagueCode).size();
    }

    /**
     * Gets the collection of products to use for the specified series of the specified league.
     * @param leagueCode the league code
     * @param seriesIndex the series index (i.e. week number)
     * @return the collection
     */
    public CardCollection getCollectionForSeries(String leagueCode, int seriesIndex) {
        if (_fixedFromPool.get(leagueCode)==null) {
            return _collections.get(leagueCode).get(seriesIndex);
        } else {
            MutableCardCollection collection = new DefaultCardCollection(_collections.get(leagueCode).get(seriesIndex));
            if (_fixedFromPool.get(leagueCode).get(seriesIndex)!=null) {
                for (PoolIntPair pair : _fixedFromPool.get(leagueCode).get(seriesIndex)) {
                    List<String> pool = new LinkedList(pair.getPool());
                    Collections.shuffle(pool);
                    for (int i = 0; i < pair.getCount(); i++) {
                        if (pool.get(i) != null) {
                            if (pool.get(i).startsWith("LIGHTSPEED_")) {
                                addLightspeedSealedPacket(collection, pool.get(i));
                            } else {
                                collection.addItem(pool.get(i), 1);
                            }
                        }
                    }
                }
            }
            return collection;
        }
    }

    private class PoolIntPair {
        private List<String> _cards;
        private int _toAdd;
        public PoolIntPair(List<String> cards, int toAdd) {
            _cards = new LinkedList<>();
            _cards.addAll(cards);
            _toAdd = toAdd;
        }

        public List<String> getPool() {
            return _cards;
        }

        public int getCount() {
            return _toAdd;
        }
    }
}
