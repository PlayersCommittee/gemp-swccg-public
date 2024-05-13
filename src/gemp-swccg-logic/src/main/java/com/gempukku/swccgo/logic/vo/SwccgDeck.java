package com.gempukku.swccgo.logic.vo;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a Star Wars CCG deck.
 */
public class SwccgDeck {
    private String _deckName;
    private List<String> _cards = new ArrayList<String>();
    private List<String> _cardsOutsideDeck = new ArrayList<String>();

    /**
     * Create a SwccgDeck object with the specified deck name.
     * @param deckName the deck name
     */
    public SwccgDeck(String deckName) {
        _deckName = deckName;
    }

    public String getDeckName() {
        return _deckName;
    }

    public void addCard(String card) {
        _cards.add(card);
    }

    public void addCardOutsideDeck(String card) {
        _cardsOutsideDeck.add(card);
    }

    public List<String> getCards() {
        return Collections.unmodifiableList(_cards);
    }

    public List<String> getCardsOutsideDeck() {
        return Collections.unmodifiableList(_cardsOutsideDeck);
    }

    public Side getSide(SwccgCardBlueprintLibrary _library) {
        boolean containsDarkCards = false;
        boolean containsLightCards = false;

        for (String cardId : _cards) {
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(cardId);
            if (blueprint != null) {
                Side side = blueprint.getSide();
                if (side == Side.DARK) {
                    containsDarkCards = true;
                    if (containsLightCards) {
                        return null;
                    }
                } else if (side == Side.LIGHT) {
                    containsLightCards = true;
                    if (containsDarkCards) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        if (containsDarkCards)
            return Side.DARK;
        else if (containsLightCards)
            return Side.LIGHT;
        else
            return null;
    }

    public String toString() {
        //copied from DeckSerialization
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getCards().size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(getCards().get(i));
        }
        sb.append("|");
        for (int i = 0; i < getCardsOutsideDeck().size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(getCardsOutsideDeck().get(i));
        }

        return sb.toString();
    }
}
