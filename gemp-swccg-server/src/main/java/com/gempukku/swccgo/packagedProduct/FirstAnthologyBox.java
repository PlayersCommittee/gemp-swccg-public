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
 * Defines a First Anthology box.
 */
public class FirstAnthologyBox extends BasePackagedCardProduct {
    private SetRarity _setRarity;

    /**
     * Creates a First Anthology box.
     * @param library the blueprint library
     */
    public FirstAnthologyBox(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.FIRST_ANTHOLOGY.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.FIRST_ANTHOLOGY_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.FIRST_ANTHOLOGY_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.PREMIERE_STARTER_SET, 2);
        addProducts(result, ProductName.A_NEW_HOPE_BOOSTER_PACK, 2);
        addProducts(result, ProductName.HOTH_BOOSTER_PACK, 2);
        addProducts(result, ProductName.JEDI_PACK, 1);

        List<String> previewCards = new ArrayList<String>();
        previewCards.addAll(_setRarity.getAllCards());
        filterNonExistingCards(previewCards);
        addCards(result, previewCards, false);
        return result;
    }
}
