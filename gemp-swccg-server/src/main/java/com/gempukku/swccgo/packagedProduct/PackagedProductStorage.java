package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackagedProductStorage {
    private Map<String, PackagedCardProduct> _packagedProducts = new HashMap<String, PackagedCardProduct>();

    public void addPackagedProduct(String productName, PackagedCardProduct packagedProduct) {
        _packagedProducts.put(productName, packagedProduct);
    }

    public List<CardCollection.Item> openPackagedProduct(String productName) {
        PackagedCardProduct packagedProduct = _packagedProducts.get(productName);
        if (packagedProduct == null)
            return null;
        return packagedProduct.openPackage();
    }
}
