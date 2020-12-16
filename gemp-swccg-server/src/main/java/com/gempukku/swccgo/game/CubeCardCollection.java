package com.gempukku.swccgo.game;

import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;

import java.util.*;

public class CubeCardCollection implements MutableCardCollection {
    private Map<String, Item> _counts = new LinkedHashMap<String, Item>();
    private int _currency;

    public CubeCardCollection() {
    }

    public CubeCardCollection(CardCollection cardCollection) {
        _counts.putAll(cardCollection.getAll());
        _currency = cardCollection.getCurrency();
    }

    @Override
    public synchronized void addCurrency(int currency) {
        _currency += currency;
    }

    @Override
    public synchronized boolean removeCurrency(int currency) {
        if (_currency < currency)
            return false;
        _currency -= currency;
        return true;
    }

    @Override
    public synchronized int getCurrency() {
        return _currency;
    }

    @Override
    public synchronized void addItem(String itemId, int toAdd) {
        if (toAdd > 0) {
            Item oldCount = _counts.get(itemId);
            if (oldCount == null)
                _counts.put(itemId, Item.createItem(itemId, toAdd));
            else
                _counts.put(itemId, Item.createItem(itemId, toAdd + oldCount.getCount()));
        }
    }

    @Override
    public synchronized boolean removeItem(String itemId, int toRemove) {
        if (toRemove > 0) {
            Item oldCount = _counts.get(itemId);
            if (oldCount == null || oldCount.getCount() < toRemove)
                return false;
            if (oldCount.getCount() == toRemove)
                _counts.remove(itemId);
            else
                _counts.put(itemId, Item.createItem(itemId, oldCount.getCount() - toRemove));
        }
        return true;
    }

    @Override
    public synchronized CardCollection openPack(String packId, String selection, PackagedProductStorage packagedProductStorage) {
        System.out.println("debug CubeCardCollection: this part is where you would do something different for Cube");
        Item count = _counts.get(packId);
        if (count == null)
            return null;
        if (count.getCount() > 0) {
            List<Item> packContents = null;
            if (selection != null && packId.startsWith("(S)")) {
                if (hasSelection(packId, selection, packagedProductStorage)) {
                    packContents = new LinkedList<Item>();
                    packContents.add(Item.createItem(selection, 1));
                }
            } else {
                packContents = packagedProductStorage.openPackagedProductWithExclusions(packId, getExclusions());
            }

            if (packContents == null)
                return null;

            CubeCardCollection packCollection = new CubeCardCollection();

            for (Item itemFromPack : packContents) {
                addItem(itemFromPack.getBlueprintId(), itemFromPack.getCount());
                packCollection.addItem(itemFromPack.getBlueprintId(), itemFromPack.getCount());
            }

            removeItem(packId, 1);

            return packCollection;
        }
        return null;
    }

    @Override
    public synchronized Map<String, Item> getAll() {
        return Collections.unmodifiableMap(_counts);
    }

    @Override
    public synchronized int getItemCount(String blueprintId) {
        Item count = _counts.get(blueprintId);
        if (count == null)
            return 0;
        return count.getCount();
    }

    private boolean hasSelection(String packId, String selection, PackagedProductStorage packagedProductStorage) {
        for (Item item : packagedProductStorage.openPackagedProduct(packId)) {
            if (item.getBlueprintId().equals(selection))
                return true;
        }
        return false;
    }

    private Set<String> getExclusions() {
        Set<String> result = new HashSet<String>();


        return result;
    }
}
