package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to choose cards accepted by the specified filter that are stacked on the
 * specified card (or stacked on a card accepted by the specified stackedOn filter).
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseStackedCardsEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private PhysicalCard _stackedOn;
    private Filterable _stackedOnFilters;
    private int _minimum;
    private int _maximum;
    private Filterable _filters;
    private boolean _doNotShowCardFront;
    private boolean _cardSelectionFailed;

    /**
     * Creates an effect that causes the player to choose cards stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseStackedCardsEffect(Action action, String playerId, PhysicalCard stackedOn, int minimum, int maximum) {
        this(action, playerId, stackedOn, minimum, maximum, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseStackedCardsEffect(Action action, String playerId, PhysicalCard stackedOn, int minimum, int maximum, Filterable filters) {
        this(action, playerId, stackedOn, minimum, maximum, filters, false);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     * @param doNotShowCardFront true if the card fronts are not to be shown, otherwise false
     */
    public ChooseStackedCardsEffect(Action action, String playerId, PhysicalCard stackedOn, int minimum, int maximum, Filterable filters, boolean doNotShowCardFront) {
        super(action);
        _playerId = playerId;
        _stackedOn = stackedOn;
        _minimum = minimum;
        _maximum = maximum;
        _filters = filters;
        _doNotShowCardFront = doNotShowCardFront;
    }

    /**
     * Creates an effect that causes the player to choose cards stacked on a card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseStackedCardsEffect(Action action, String playerId, Filterable stackedOnFilters, int minimum, int maximum) {
        this(action, playerId, stackedOnFilters, minimum, maximum, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter that are stacked on a
     * card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseStackedCardsEffect(Action action, String playerId, Filterable stackedOnFilters, int minimum, int maximum, Filterable filters) {
        this(action, playerId, stackedOnFilters, minimum, maximum, filters, false);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter that are stacked on a
     * card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseStackedCardsEffect(Action action, String playerId, Filterable stackedOnFilters, int minimum, int maximum, Filterable filters, boolean doNotShowCardFront) {
        super(action);
        _playerId = playerId;
        _stackedOnFilters = stackedOnFilters;
        _minimum = minimum;
        _maximum = maximum;
        _filters = filters;
        _doNotShowCardFront = doNotShowCardFront;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose);
    }

    /**
     * Determines whether selection are automatically made is number of cards to select is the same as the minimum number to choose
     */
    protected boolean getUseShortcut() {
        return false;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        if (_stackedOn != null)
            return !Filters.filterCount(game.getGameState().getStackedCards(_stackedOn), game, 1, _filters).isEmpty();
        else
            return !Filters.filterStacked(game, Filters.and(Filters.stackedOn(_action.getActionSource(), Filters.and(_stackedOnFilters)), _filters)).isEmpty();
    }

    protected boolean forceManualSelection() {
        return false;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        Collection<PhysicalCard> selectableCards;
        if (_stackedOn != null)
            selectableCards = Filters.filter(game.getGameState().getStackedCards(_stackedOn), game, _filters);
        else
            selectableCards = Filters.filterStacked(game, Filters.and(Filters.stackedOn(_action.getActionSource(), Filters.and(_stackedOnFilters)), _filters));

        boolean faceDown = false;
        for (PhysicalCard selectableCard : selectableCards) {
            if (selectableCard.getZone() == Zone.STACKED_FACE_DOWN) {
                faceDown = true;
                break;
            }
        }

        // Make sure at least the minimum number of cards can be found
        if (selectableCards.size() < _minimum) {
            return new FullEffectResult(false);
        }

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        final int minimum = _minimum;

        if (maximum == 0) {
            cardsSelected(game, Collections.<PhysicalCard>emptySet());
        }
        else if (selectableCards.size() == minimum && !forceManualSelection() && (getUseShortcut() || !_action.isAllowAbort())) {
            cardsSelected(game, selectableCards);
        }
        else {
            String choiceText = getChoiceText(maximum);

            // Use different select method based on if cards are face down and need to be shown
            if (faceDown && !_doNotShowCardFront) {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new ArbitraryCardsSelectionDecision(choiceText + ((minimum > 0 &&_action.isAllowAbort()) ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                _cardSelectionFailed = selectedCards.size() < minimum;
                                if (_cardSelectionFailed && _action.isAllowAbort()) {
                                    return;
                                }

                                cardsSelected(game, selectedCards);
                            }
                        });
            }
            else {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new CardsSelectionDecision(choiceText + ((minimum > 0 &&_action.isAllowAbort()) ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                _cardSelectionFailed = selectedCards.size() < minimum;
                                if (_cardSelectionFailed && _action.isAllowAbort()) {
                                    return;
                                }

                                cardsSelected(game, selectedCards);
                            }
                        });
            }
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !_cardSelectionFailed;
    }

    /**
     * This method is called when cards have been selected.
     * @param game the game
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
