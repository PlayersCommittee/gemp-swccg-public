package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.*;

/**
 * An effect that causes the specified player to choose cards on the table.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the card chosen.
 */
public abstract class ChooseCardsOnTableEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private int _maximumAcceptsCount;
    private boolean _matchPartialModelType;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Collection<PhysicalCard> _cards;
    private Filterable _filters;
    private String _choiceText;
    private boolean cardSelectionFailed;

    /**
     * Creates an effect that causes the player to choose cards from the specified collection of cards on the table.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param cards the cards to choose from
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Collection<PhysicalCard> cards) {
        this(action, playerId, choiceText, minimum, maximum, cards, Filters.onTable);
    }

    /**
     * Creates an effect that causes the player to choose cards from the specified collection of cards on the table accepted
     * by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param cards the cards to choose from
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Collection<PhysicalCard> cards, Filterable filters) {
        this(action, playerId, choiceText, minimum, maximum, Integer.MAX_VALUE, true, cards, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards from the specified collection of cards on the table accepted
     * by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param cards the cards to choose from
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Collection<PhysicalCard> cards, Filterable filters) {
        super(action);
        _playerId = playerId;
        _choiceText = choiceText;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximumAcceptsCount;
        _matchPartialModelType = matchPartialModelType;
        _cards = cards;
        _filters = Filters.and(Filters.onTable, filters);
    }

    /**
     * Creates an effect that causes the player to choose from cards on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Filterable filters) {
        this(action, playerId, choiceText, minimum, maximum, (Map<InactiveReason, Boolean>) null, filters);
    }

    /**
     * Creates an effect that causes the player to choose from cards on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Filterable filters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, (Map<InactiveReason, Boolean>) null, filters);
    }

    /**
     * Creates an effect that causes the player to choose from cards on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param spotOverrides overrides for which inactive cards are visible
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        this(action, playerId, choiceText, minimum, maximum, Integer.MAX_VALUE, true, spotOverrides, filters);
    }

    /**
     * Creates an effect that causes the player to choose from cards on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides overrides for which inactive cards are visible
     * @param filters the filter
     */
    public ChooseCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        super(action);
        _playerId = playerId;
        _choiceText = choiceText;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximumAcceptsCount;
        _matchPartialModelType = matchPartialModelType;
        _spotOverrides = spotOverrides;
        _filters = filters;
    }

    /**
     * Determines whether selection are automatically made is number of cards to select is the same as the minimum number to choose
     */
    protected boolean getUseShortcut() {
        return false;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        if (_cards != null)
            return !Filters.filterCount(_cards, game, _minimum, _filters).isEmpty();
        else
            return Filters.canSpot(game, _action.getActionSource(), _minimum, _spotOverrides, _filters);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // If player is not set, set to current player to make choices
        if (_playerId == null) {
            _playerId = game.getGameState().getCurrentPlayerId();
        }

        // Determine the cards to choose from
        Collection<PhysicalCard> selectableCards;
        if (_cards != null)
            selectableCards = Filters.filter(_cards, game, _matchPartialModelType, _filters);
        else
            selectableCards = Filters.filterActive(game, _action.getActionSource(), _matchPartialModelType, _spotOverrides, _filters);

        // Filter cards by accounting for cards with multiple classes
        int acceptsCountSoFar = 0;
        List<PhysicalCard> validCards = new LinkedList<PhysicalCard>();
        for (PhysicalCard selectableCard : selectableCards) {
            int acceptsCount = Filters.and(_filters).acceptsCount(game, selectableCard);
            if (acceptsCount > 0 && acceptsCount <= _maximumAcceptsCount) {
                validCards.add(selectableCard);
                acceptsCountSoFar += acceptsCount;
            }
        }
        selectableCards = validCards;

        // Make sure at least the minimum number of cards can be found
        if (acceptsCountSoFar < _minimum) {
            return new FullEffectResult(false);
        }

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        final int minimum = _minimum;

        if (maximum == 0) {
            cardsSelected(Collections.<PhysicalCard>emptySet());
        }
        else if (selectableCards.size() == minimum && (getUseShortcut() || !_action.isAllowAbort())) {
            cardsSelected(selectableCards);
        }
        else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(_choiceText + ((minimum > 0 && _action.isAllowAbort()) ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            int numSelected = Filters.count(selectedCards, game, true, _filters);
                            cardSelectionFailed = numSelected < minimum;
                            if (cardSelectionFailed && _action.isAllowAbort()) {
                                return;
                            }

                            cardsSelected(selectedCards);
                        }
                    });
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !cardSelectionFailed;
    }

    /**
     * This method is called when cards have been selected.
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(Collection<PhysicalCard> selectedCards);
}
