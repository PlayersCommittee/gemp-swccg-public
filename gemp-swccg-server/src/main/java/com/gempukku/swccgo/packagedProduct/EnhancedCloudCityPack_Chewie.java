package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines an Enhanced Cloud City pack (Chewie With Blaster Rifle).
 */
public class EnhancedCloudCityPack_Chewie extends BasePackagedCardProduct {
    private List<String> _premiumCards = new ArrayList<String>();

    /**
     * Creates an Enhanced Cloud City pack (Chewie With Blaster Rifle).
     * @param library the blueprint library
     */
    public EnhancedCloudCityPack_Chewie(SwccgCardBlueprintLibrary library) {
        super(library);
        _premiumCards.add("109_1");
        _premiumCards.add("109_2");
        _premiumCards.add("109_4");

        filterNonExistingCards(_premiumCards);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.ENHANCED_CLOUD_CITY_PACK_CHEWIE;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.ENHANCED_CLOUD_CITY_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addCards(result, _premiumCards, false);
        addProducts(result, ProductName.CLOUD_CITY_BOOSTER_PACK, 4);
        return result;
    }
}
