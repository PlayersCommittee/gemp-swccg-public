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
 * Defines a Third Anthology box.
 */
public class ThirdAnthologyBox extends BasePackagedCardProduct {
    private SetRarity _setRarity;

    /**
     * Creates a Third Anthology box.
     * @param library the blueprint library
     */
    public ThirdAnthologyBox(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.THIRD_ANTHOLOGY.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.THIRD_ANTHOLOGY_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.THIRD_ANTHOLOGY_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.SPECIAL_EDITION_LIGHT_STARTER_DECK, 1);
        addProducts(result, ProductName.SPECIAL_EDITION_DARK_STARTER_DECK, 1);
        addProducts(result, ProductName.JABBAS_PALACE_BOOSTER_PACK, 2);
        addProducts(result, ProductName.PREMIERE_BOOSTER_PACK, 2);

        List<String> previewCards = new ArrayList<String>();
        previewCards.addAll(_setRarity.getAllCards());
        filterNonExistingCards(previewCards);
        addCards(result, previewCards, false);
        return result;
    }
}
