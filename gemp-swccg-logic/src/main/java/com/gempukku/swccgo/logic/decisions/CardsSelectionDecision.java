package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A decision that involves choosing cards from the table (or hand) on the User Interface.
 */
public abstract class CardsSelectionDecision extends AbstractAwaitingDecision {
    private List<PhysicalCard> _physicalCards;
    private int _minimum;
    private int _maximum;

    /**
     * Creates a decision that involves choosing cards from the table (or hand) on the User Interface.
     * @param text the text to show the player making the decision
     * @param physicalCards the cards to choose from
     */
    public CardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards) {
        this(text, physicalCards, 0, physicalCards.size());
    }

    /**
     * Creates a decision that involves choosing a specified number of cards from the table (or hand) on the User Interface.
     * @param text the text to show the player making the decision
     * @param physicalCards the cards to choose from
     * @param minimum the minimum number of cards to select
     * @param maximum the maximum number of cards to select
     */
    public CardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards, int minimum, int maximum) {
        super(1, text, AwaitingDecisionType.CARD_SELECTION);
        _physicalCards = new LinkedList<PhysicalCard>(physicalCards);
        _minimum = minimum;
        _maximum = maximum;
        setParam("min", String.valueOf(minimum));
        setParam("max", String.valueOf(maximum));
        setParam("cardId", getCardIds(_physicalCards));
    }

    /**
     * Gets an array of card ids.
     * @param physicalCards the cards
     * @return the card ids
     */
    private String[] getCardIds(List<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        for (int i = 0; i < physicalCards.size(); i++)
            result[i] = String.valueOf(physicalCards.get(i).getCardId());
        return result;
    }

    /**
     * Gets the cards the player selected during the decision.
     * @param response the response
     * @return the cards selected
     * @throws DecisionResultInvalidException
     */
    protected List<PhysicalCard> getSelectedCardsByResponse(String response) throws DecisionResultInvalidException {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        if (response.isEmpty()) {
            if (_minimum == 0)
                return result;
            else
                throw new DecisionResultInvalidException();
        }
        String[] cardIds = response.split(",");
        if (cardIds.length < _minimum || cardIds.length > _maximum)
            throw new DecisionResultInvalidException();

        try {
            for (String cardId : cardIds) {
                PhysicalCard card = getSelectedCardById(Integer.parseInt(cardId));
                if (result.contains(card))
                    throw new DecisionResultInvalidException();
                result.add(card);
            }
        } catch (NumberFormatException e) {
            throw new DecisionResultInvalidException();
        }

        return result;
    }

    /**
     * Gets the selected card by id
     * @param cardId the card id
     * @return the card
     * @throws DecisionResultInvalidException
     */
    private PhysicalCard getSelectedCardById(int cardId) throws DecisionResultInvalidException {
        for (PhysicalCard physicalCard : _physicalCards)
            if (physicalCard.getCardId() == cardId)
                return physicalCard;

        throw new DecisionResultInvalidException();
    }
}
