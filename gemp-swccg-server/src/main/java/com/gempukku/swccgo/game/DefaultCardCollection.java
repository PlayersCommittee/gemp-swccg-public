package com.gempukku.swccgo.game;

import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;

import java.util.*;

public class DefaultCardCollection implements MutableCardCollection {
    private Map<String, Item> _counts = new LinkedHashMap<String, Item>();
    private int _currency;
    private boolean _excludePackDuplicates;

    public DefaultCardCollection() {
        this(false);
    }

    public DefaultCardCollection(boolean excludePackDuplicates) {
        _excludePackDuplicates = excludePackDuplicates;
        System.out.println("debug: creating DefaultCardCollection("+String.valueOf(_excludePackDuplicates)+")");
    }

    public DefaultCardCollection(CardCollection cardCollection) {
        _excludePackDuplicates = cardCollection.excludePackDuplicates();
        _counts.putAll(cardCollection.getAll());
        _currency = cardCollection.getCurrency();
        System.out.println("debug: creating DefaultCardCollection(cardCollection,"+String.valueOf(_excludePackDuplicates)+")");
    }

    @Override
    public boolean excludePackDuplicates() {
        return _excludePackDuplicates;
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
        if(_excludePackDuplicates) {
            System.out.println("debug: should be doing something different for Cube");
        }
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
            } else if(_excludePackDuplicates) {
                System.out.println("debug: got to the second part");
                packContents = packagedProductStorage.openPackagedProductWithExclusions(packId, getExclusions());
            } else {
                packContents = packagedProductStorage.openPackagedProduct(packId);
            }


            if (packContents == null)
                return null;

            DefaultCardCollection packCollection = new DefaultCardCollection(_excludePackDuplicates);

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

    private List<String> getExclusions() {
        if(!_excludePackDuplicates)
            return Collections.emptyList();

        List<String> result = new LinkedList<String>();
        for(String item: _counts.keySet()) {
            int count = getItemCount(item);
            for(int i=0;i<count;i++) {
                result.add(item);
            }
        }

        return result;
    }
}
