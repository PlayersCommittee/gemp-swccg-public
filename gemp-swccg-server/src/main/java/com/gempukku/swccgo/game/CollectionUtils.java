package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
    public static Map<String, Integer> getTotalCardCountForDeck(SwccgDeck deck) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String cards : deck.getCards())
            incrementCardCount(counts, cards, 1);
        for (String cards : deck.getCardsOutsideDeck())
            incrementCardCount(counts, cards, 1);
        return counts;
    }

    public static Map<String, Integer> getTotalCardCount(List<String> cards) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String card : cards)
            incrementCardCount(counts, card, 1);
        return counts;
    }

    private static void incrementCardCount(Map<String, Integer> map, String blueprintId, int incrementBy) {
        final Integer oldCount = map.get(blueprintId);
        if (oldCount == null)
            map.put(blueprintId, incrementBy);
        else
            map.put(blueprintId, oldCount + incrementBy);
    }
}
