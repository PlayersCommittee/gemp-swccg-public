package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FixedPackBox implements PackagedCardProduct {
    private Map<String, Integer> _contents = new LinkedHashMap<String, Integer>();

    public FixedPackBox(String packName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FixedPackBox.class.getResourceAsStream("/" + packName + ".pack")));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    String[] result = line.split("x", 2);
                    _contents.put(result[1], Integer.parseInt(result[0]));
                }
            }
        } finally {
            bufferedReader.close();
        }

    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return null;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return 0;
    }

    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        for (Map.Entry<String, Integer> contentsEntry : _contents.entrySet()) {
            String blueprintId = contentsEntry.getKey();
            result.add(CardCollection.Item.createItem(blueprintId, contentsEntry.getValue()));
        }
        return result;
    }

    @Override
    public List<CardCollection.Item> openPackageWithExclusions(Set<String> exclusions) {
        return null;
    }
}
