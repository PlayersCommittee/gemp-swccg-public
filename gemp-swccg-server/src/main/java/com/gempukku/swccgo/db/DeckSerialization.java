package com.gempukku.swccgo.db;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

public class DeckSerialization {
    public static String buildContentsFromDeck(SwccgDeck deck) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deck.getCards().size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(deck.getCards().get(i));
        }
        sb.append("|");
        for (int i = 0; i < deck.getCardsOutsideDeck().size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(deck.getCardsOutsideDeck().get(i));
        }

        return sb.toString();
    }

    public static SwccgDeck buildDeckFromContents(String deckName, String contents) {
        // New format
        String[] parts = contents.split("\\|");

        SwccgDeck deck = new SwccgDeck(deckName);
        if (parts.length > 0 && !parts[0].isEmpty()) {
            for (String card : parts[0].split(",")) {
                if (!card.equals(""))
                    deck.addCard(card);
            }
        }
        if (parts.length > 1 && !parts[1].isEmpty()) {
            for (String card : parts[1].split(",")) {
                if (!card.equals(""))
                    deck.addCardOutsideDeck(card);
            }
        }

        return deck;
    }
}
