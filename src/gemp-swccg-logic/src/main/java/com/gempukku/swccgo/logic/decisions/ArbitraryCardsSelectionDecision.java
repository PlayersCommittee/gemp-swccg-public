package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;

import java.util.*;

/**
 * A decision that involves choosing cards from a pop-up window in the User Interface.
 */
public abstract class ArbitraryCardsSelectionDecision extends AbstractAwaitingDecision {
    private Collection<PhysicalCard> _physicalCards;
    private Collection<PhysicalCard> _selectable;
    private int _minimum;
    private int _maximum;
    private boolean _returnAnyChange;

    /**
     * Creates a decision to choose any number of the specified cards.
     * @param text the text to show the player making the decision
     * @param physicalCards the cards
     */
    public ArbitraryCardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards) {
        this(text, physicalCards, 0, physicalCards.size());
    }

    /**
     * Creates a decision to choose a specified number of cards from the specified cards.
     * @param text the text to show the player making the decision
     * @param physicalCards the cards
     * @param minimum the minimum number of cards to select
     * @param maximum the maximum number of cards to select
     */
    public ArbitraryCardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards, int minimum, int maximum) {
        this(text, physicalCards, physicalCards, minimum, maximum);
    }

    /**
     * Creates a decision to choose a specified number of cards from the selectable cards (while cards in physicalCards,
     * but not in selectable are shown to the player but not selectable).
     * @param text the text to show the player making the decision
     * @param physicalCards the cards to show
     * @param selectable the cards that are selectable (subset of physicalCards)
     * @param minimum the minimum number of cards to select
     * @param maximum the maximum number of cards to select
     */
    public ArbitraryCardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards, Collection<PhysicalCard> selectable, int minimum, int maximum) {
        this(text, physicalCards, selectable, minimum, maximum, null);
    }

    /**
     * Creates a decision to choose a specified number of cards from the selectable cards (while cards in physicalCards,
     * but not in selectable are shown to the player but not selectable).
     * @param text the text to show the player making the decision
     * @param physicalCards the cards to show
     * @param selectable the cards that are selectable (subset of physicalCards)
     * @param minimum the minimum number of cards to select
     * @param maximum the maximum number of cards to select
     * @param texts the text to show for each card
     */
    public ArbitraryCardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards, Collection<PhysicalCard> selectable, int minimum, int maximum, Map<PhysicalCard, String> texts) {
        this(text, physicalCards, Collections.<PhysicalCard>emptyList(), selectable, minimum, maximum, false, texts);
    }

    /**
     * Creates a decision to choose a specified number of cards from the selectable cards (while cards in physicalCards,
     * but not in selectable are shown to the player but not selectable).
     * @param text the text to show the player making the decision
     * @param physicalCards the cards to show
     * @param preselected the cards that are pre-selected (subset of physicalCards)
     * @param selectable the cards that are selectable (subset of physicalCards)
     * @param returnAnyChange true if user interface should return
     * @param minimum the minimum number of cards to select
     * @param maximum the maximum number of cards to select
     * @param texts the text to show for each card
     */
    public ArbitraryCardsSelectionDecision(String text, Collection<PhysicalCard> physicalCards, Collection<PhysicalCard> preselected, Collection<PhysicalCard> selectable, int minimum, int maximum, boolean returnAnyChange, Map<PhysicalCard, String> texts) {
        super(1, text, AwaitingDecisionType.ARBITRARY_CARDS);
        _physicalCards = physicalCards;
        _selectable = selectable;
        _minimum = minimum;
        _maximum = maximum;
        _returnAnyChange = returnAnyChange;
        setParam("min", String.valueOf(minimum));
        setParam("max", String.valueOf(maximum));
        setParam("returnAnyChange", String.valueOf(returnAnyChange));
        setParam("cardId", getCardIds(physicalCards));
        setParam("blueprintId", getBlueprintIds(physicalCards));
        setParam("testingText", getTestingTexts(physicalCards));
        setParam("backSideTestingText", getBackSideTestingTexts(physicalCards));
        setParam("horizontal", getHorizontals(physicalCards));
        setParam("preselected", getPreselected(physicalCards, preselected));
        setParam("selectable", getSelectable(physicalCards, selectable));
        setParam("cardText", getCardTexts(physicalCards, texts));
    }

    /**
     * Gets the pre-selected cards out of the cards shown.
     * @param physicalCards the cards to show
     * @param preselected the cards that are pre-selected
     * @return the cards that are pre-selected
     */
    private String[] getPreselected(Collection<PhysicalCard> physicalCards, Collection<PhysicalCard> preselected) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = String.valueOf(preselected.contains(physicalCard));
            index++;
        }
        return result;
    }

    /**
     * Gets the selectable cards out of the cards shown.
     * @param physicalCards the cards to show
     * @param selectable the cards that are selectable
     * @return the cards that are selectable
     */
    private String[] getSelectable(Collection<PhysicalCard> physicalCards, Collection<PhysicalCard> selectable) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = String.valueOf(selectable.contains(physicalCard));
            index++;
        }
        return result;
    }

    /**
     * Gets an array of temp card ids.
     * @param physicalCards the cards
     * @return the temp card ids
     */
    private String[] getCardIds(Collection<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        for (int i = 0; i < physicalCards.size(); i++)
            result[i] = "temp" + i;
        return result;
    }

    /**
     * Gets the card blueprint ids.
     * @param physicalCards the cards
     * @return the card blueprint ids
     */
    private String[] getBlueprintIds(Collection<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = physicalCard.getBlueprintId(physicalCard.getBlueprint().getCardCategory()!= CardCategory.OBJECTIVE);
            index++;
        }
        return result;
    }

    /**
     * Gets the card testing texts.
     * @param physicalCards the cards
     * @return the card testing texts
     */
    private String[] getTestingTexts(Collection<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = String.valueOf(physicalCard.getTestingText(null, physicalCard.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE, false));
            index++;
        }
        return result;
    }

    /**
     * Gets the card backside testing texts.
     * @param physicalCards the cards
     * @return the card testing texts
     */
    private String[] getBackSideTestingTexts(Collection<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = String.valueOf(physicalCard.getTestingText(null, physicalCard.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE, true));
            index++;
        }
        return result;
    }

    /**
     * Gets the card horizontals.
     * @param physicalCards the cards
     * @return the card horizontals
     */
    private String[] getHorizontals(Collection<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            result[index] = String.valueOf(physicalCard.getBlueprint().isHorizontal());
            index++;
        }
        return result;
    }

    /**
     * Gets the text to show for each card.
     * @param physicalCards the cards to show
     * @param texts the card texts
     * @return the cards that are selectable
     */
    private String[] getCardTexts(Collection<PhysicalCard> physicalCards, Map<PhysicalCard, String> texts) {
        String[] result = new String[physicalCards.size()];
        int index = 0;
        for (PhysicalCard physicalCard : physicalCards) {
            if (texts != null && texts.containsKey(physicalCard))
                result[index] = texts.get(physicalCard);
            else
                result[index] = "";
            index++;
        }
        return result;
    }

    /**
     * Gets the card by index.
     * @param index the index
     * @return the card
     */
    protected PhysicalCard getPhysicalCardByIndex(int index) {
        int i = 0;
        for (PhysicalCard physicalCard : _physicalCards) {
            if (i == index)
                return physicalCard;
            i++;
        }
        return null;
    }

    /**
     * Gets the cards the player selected during the decision.
     * @param response the response
     * @return the list of cards selected
     * @throws DecisionResultInvalidException
     */
    protected List<PhysicalCard> getSelectedCardsByResponse(String response) throws DecisionResultInvalidException {
        String[] cardIds;
        if (response.isEmpty())
            cardIds = new String[0];
        else
            cardIds = response.split(",");

        if (!_returnAnyChange && (cardIds.length < _minimum || cardIds.length > _maximum))
            throw new DecisionResultInvalidException();

        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        try {
            for (String cardId : cardIds) {
                PhysicalCard card = getPhysicalCardByIndex(Integer.parseInt(cardId.substring(4)));
                if (result.contains(card) || !_selectable.contains(card))
                    throw new DecisionResultInvalidException();
                result.add(card);
            }
        } catch (NumberFormatException e) {
            throw new DecisionResultInvalidException();
        } catch (IndexOutOfBoundsException e) {
            throw new DecisionResultInvalidException();
        }

        return result;
    }

    /**
     * Gets the card index the player selected during the decision.
     * @param response the response
     * @return the list of indexes
     * @throws DecisionResultInvalidException
     */
    protected List<Integer> getIndexesByResponse(String response) throws DecisionResultInvalidException {
        String[] cardIds;
        if (response.isEmpty())
            cardIds = new String[0];
        else
            cardIds = response.split(",");

        if (!_returnAnyChange && (cardIds.length < _minimum || cardIds.length > _maximum))
            throw new DecisionResultInvalidException();

        List<Integer> result = new LinkedList<Integer>();
        try {
            for (String cardId : cardIds) {
                Integer index = Integer.parseInt(cardId.substring(4));
                PhysicalCard card = getPhysicalCardByIndex(index);
                if (result.contains(index) || !_selectable.contains(card))
                    throw new DecisionResultInvalidException();
                result.add(index);
            }
        } catch (NumberFormatException e) {
            throw new DecisionResultInvalidException();
        } catch (IndexOutOfBoundsException e) {
            throw new DecisionResultInvalidException();
        }

        return result;
    }
}

