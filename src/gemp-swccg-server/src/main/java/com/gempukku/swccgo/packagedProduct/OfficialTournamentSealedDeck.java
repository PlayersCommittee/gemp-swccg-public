package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines an Official Tournament Sealed Deck box.
 */
public class OfficialTournamentSealedDeck extends BasePackagedCardProduct {
    private SetRarity _setRarity;

    /**
     * Creates an Official Tournament Sealed Deck box.
     * @param library the blueprint library
     */
    public OfficialTournamentSealedDeck(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.OTSD.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.OFFICIAL_TOURNAMENT_SEALED_DECK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.OFFICIAL_TOURNAMENT_SEALED_DECK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.PREMIERE_BOOSTER_PACK, 4);
        addProducts(result, ProductName.A_NEW_HOPE_BOOSTER_PACK, 1);

        List<String> premiumCards = new ArrayList<String>();
        premiumCards.addAll(_setRarity.getAllCards());
        filterNonExistingCards(premiumCards);
        addCards(result, premiumCards, false);
        return result;
    }
}
