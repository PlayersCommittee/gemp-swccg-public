package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines an Enhanced Cloud City pack (Boba Fett In Slave One).
 */
public class EnhancedCloudCityPack_BobaFett extends BasePackagedCardProduct {
    private List<String> _premiumCards = new ArrayList<String>();

    /**
     * Creates an Enhanced Cloud City pack (Boba Fett In Slave One).
     * @param library the blueprint library
     */
    public EnhancedCloudCityPack_BobaFett(SwccgCardBlueprintLibrary library) {
        super(library);
        _premiumCards.add("109_8");
        _premiumCards.add("109_6");
        _premiumCards.add("109_7");

        filterNonExistingCards(_premiumCards);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.ENHANCED_CLOUD_CITY_PACK_BOBA_FETT;
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
