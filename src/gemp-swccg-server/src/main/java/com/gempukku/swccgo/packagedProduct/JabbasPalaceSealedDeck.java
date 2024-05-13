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
 * Defines a Jabba's Palace Sealed Deck box.
 */
public class JabbasPalaceSealedDeck extends BasePackagedCardProduct {
    private SetRarity _setRarity;

    /**
     * Creates a Jabba's Palace Sealed Deck box.
     * @param library the blueprint library
     */
    public JabbasPalaceSealedDeck(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.JPSD.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     *
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.JABBAS_PALACE_SEALED_DECK;
    }

    /**
     * Gets the price of the product.
     *
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.JABBAS_PALACE_SEALED_DECK;
    }

    /**
     * Opens the packaged card product.
     *
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.JABBAS_PALACE_BOOSTER_PACK, 6);

        List<String> premiumCards = new ArrayList<String>();
        premiumCards.addAll(_setRarity.getAllCards());
        filterNonExistingCards(premiumCards);
        addCards(result, premiumCards, false);
        return result;
    }
}