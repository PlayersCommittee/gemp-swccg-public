package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines an A New Hope booster box.
 */
public class ANewHopeBoosterBox extends BasePackagedCardProduct {

    /**
     * Creates an A New Hope booster box.
     * @param library the blueprint library
     */
    public ANewHopeBoosterBox(SwccgCardBlueprintLibrary library) {
        super(library);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.A_NEW_HOPE_BOOSTER_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.A_NEW_HOPE_BOOSTER_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.A_NEW_HOPE_BOOSTER_PACK, 36);
        return result;
    }
}
