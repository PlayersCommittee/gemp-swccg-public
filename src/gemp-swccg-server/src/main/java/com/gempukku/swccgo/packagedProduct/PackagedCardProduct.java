package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;

import java.util.List;

/**
 * An interface that defines the methods that a packaged card product (Booster Pack, Booster Box, etc.) must implement.
 */
public interface PackagedCardProduct {

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    String getProductName();

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    float getProductPrice();

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    List<CardCollection.Item> openPackage();

    /**
     * Opens the packaged card product with exclusions.
     * @param exclusions the set of items to be excluded
     * @return the card collection items contained in the packaged card product.
     */
    List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions);
}
