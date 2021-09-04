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

        List<CardCollection> noveltySealed = new ArrayList<CardCollection>();
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


        noveltySealed.add(firstWeek);

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

        noveltySealed.add(secondWeek);

        List<PoolIntPair> secondWeekPools = new LinkedList<>();
        //nothing
        pools.add(secondWeekPools);

        _fixedFromPool.put(SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getSealedCode(), pools);

        _collections.put(SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getSealedCode(), noveltySealed);
    }

    /**
     * Creates the collection of products to use for novelty sealed
     */
    private void createNoveltySealed() {

        //current novelty sealed:
        //  All Of The Jedi (2 series)
        //  series 1 = 2x each Decipher booster pack, some locations pulled from fixed pools
        //  series 2 = 1x each Decipher booster pack

        List<CardCollection> noveltySealed = new ArrayList<CardCollection>();
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


        noveltySealed.add(firstWeek);

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

        noveltySealed.add(secondWeek);

        List<PoolIntPair> secondWeekPools = new LinkedList<>();
        //nothing
        pools.add(secondWeekPools);

        _fixedFromPool.put(SealedLeagueType.NOVELTY_SEALED.getSealedCode(), pools);

        _collections.put(SealedLeagueType.NOVELTY_SEALED.getSealedCode(), noveltySealed);
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
                            collection.addItem(pool.get(i), 1);
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
