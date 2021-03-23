package com.gempukku.swccgo.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SumCardCollection implements CardCollection {
    private List<CardCollection> _cardCollections;

    public SumCardCollection(List<CardCollection> cardCollections) {
        _cardCollections = cardCollections;
    }

    @Override
    public int getCurrency() {
        int sum = 0;
        for (CardCollection cardCollection : _cardCollections)
            sum += cardCollection.getCurrency();

        return sum;
    }

    @Override
    public Map<String, Item> getAll() {
        Map<String, Item> sum = new HashMap<String, Item>();
        for (CardCollection cardCollection : _cardCollections) {
            Map<String, Item> inCollection = cardCollection.getAll();
            for (Map.Entry<String, Item> cardCount : inCollection.entrySet()) {
                String cardId = cardCount.getKey();
                Integer count = sum.get(cardId).getCount();
                if (count != null)
                    sum.put(cardId, Item.createItem(cardId, count + cardCount.getValue().getCount()));
                else
                    sum.put(cardId, Item.createItem(cardId, cardCount.getValue().getCount()));
            }
        }

        return sum;
    }

    @Override
    public int getItemCount(String blueprintId) {
        int sum = 0;
        for (CardCollection cardCollection : _cardCollections)
            sum += cardCollection.getItemCount(blueprintId);

        return sum;
    }

    @Override
    public boolean excludePackDuplicates() {
        return false;
    }
}
