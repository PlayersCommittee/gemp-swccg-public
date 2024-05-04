package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Coruscant booster box.
 */
public class CoruscantBoosterBox extends BasePackagedCardProduct {

    /**
     * Creates a Coruscant booster box.
     * @param library the blueprint library
     */
    public CoruscantBoosterBox(SwccgCardBlueprintLibrary library) {
        super(library);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.CORUSCANT_BOOSTER_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.CORUSCANT_BOOSTER_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.CORUSCANT_BOOSTER_PACK, 30);
        return result;
    }
}
