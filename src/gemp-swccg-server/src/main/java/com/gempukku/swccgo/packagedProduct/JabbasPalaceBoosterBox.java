package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Jabbas Palace booster box.
 */
public class JabbasPalaceBoosterBox extends BasePackagedCardProduct {

    /**
     * Creates a Jabba's Palace booster box.
     * @param library the blueprint library
     */
    public JabbasPalaceBoosterBox(SwccgCardBlueprintLibrary library) {
        super(library);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.JABBAS_PALACE_BOOSTER_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.JABBAS_PALACE_BOOSTER_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addProducts(result, ProductName.JABBAS_PALACE_BOOSTER_PACK, 60);
        return result;
    }
}
