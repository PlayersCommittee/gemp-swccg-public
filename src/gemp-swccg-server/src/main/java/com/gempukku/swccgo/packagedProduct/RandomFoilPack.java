package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomFoilPack implements PackagedCardProduct {
    private List<String> _availableCards = new ArrayList<String>();

    public RandomFoilPack(Rarity rarity, String[] sets) {
        RarityReader rarityReader = new RarityReader();
        for (String set : sets) {
            final SetRarity setRarity = rarityReader.getSetRarity(set);
            _availableCards.addAll(setRarity.getCardsOfRarity(rarity));
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
        final String cardBlueprintId = _availableCards.get(new Random().nextInt(_availableCards.size())) + "*";
        result.add(CardCollection.Item.createItem(cardBlueprintId, 1));
        return result;
    }

    @Override
    public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        List<String> availableWithExclusions = new ArrayList<String>();
        availableWithExclusions.addAll(_availableCards);
        for(String s:exclusions) {
            availableWithExclusions.remove(s);
        }
        final String cardBlueprintId = availableWithExclusions.get(new Random().nextInt(_availableCards.size())) + "*";
        result.add(CardCollection.Item.createItem(cardBlueprintId, 1));
        return result;
    }
}
