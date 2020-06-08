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
