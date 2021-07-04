package com.gempukku.swccgo.league;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.packagedProduct.ProductName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the sealed league product to use for sealed leagues.
 */
public class SealedLeagueProduct {
    private Map<String, List<CardCollection>> _collections = new HashMap<String, List<CardCollection>>();

    /**
     * Creates the sealed league project to use for sealed leagues.
     */
    public SealedLeagueProduct() {
        createPremiereAnhSealed();
        createJpSealed();
        createEndorDsIISealed();
        createEpisodeISealed();
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
        firstWeek.addItem("11_97",1); //Sebulba's Podracer
        firstWeek.addItem("11_47",1); //Anakin's Podracer
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
        return _collections.get(leagueCode).get(seriesIndex);
    }
}
