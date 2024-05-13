package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to choose cards from hand or on table.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromHandOrOnTableEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private String _handOwner;
    private int _minimum;
    private int _maximum;
    private Filterable _filters;
    private boolean cardSelectionFailed;

    /**
     * Creates an effect that causes the player to choose cards from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseCardsFromHandOrOnTableEffect(Action action, String playerId, int minimum, int maximum) {
        this(action, playerId, minimum, maximum, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards from hand or on table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromHandOrOnTableEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        this(action, playerId, playerId, minimum, maximum, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards from specified player's hand or on table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the handOwner
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromHandOrOnTableEffect(Action action, String playerId, String handOwner, int minimum, int maximum, Filterable filters) {
        super(action);
        _playerId = playerId;
        _handOwner = handOwner;
        _minimum = minimum;
        _maximum = maximum;
        _filters = filters;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " from hand or on table";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_handOwner).isEmpty() || Filters.canSpot(game, _action.getActionSource(), _minimum, _filters);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        List<PhysicalCard> selectableCards = new ArrayList<PhysicalCard>();
        selectableCards.addAll(Filters.filter(game.getGameState().getHand(_handOwner), game, _filters));
        selectableCards.addAll(Filters.filterActive(game, _action.getActionSource(), _filters));

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        int minimum = _minimum;

        String choiceText = getChoiceText(maximum);

        if (_handOwner.equals(_playerId)) {

            // Make sure at least the minimum number of cards can be found
            if (selectableCards.size() < _minimum) {
                return new FullEffectResult(false);
            }

            if (maximum == 0) {
                cardsSelected(game, Collections.<PhysicalCard>emptySet());
            }
            else if (selectableCards.size() == minimum && !_action.isAllowAbort()) {
                cardsSelected(game, selectableCards);
            }
            else {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new CardsSelectionDecision(choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                cardSelectionFailed = selectedCards.size() < _minimum;
                                if (cardSelectionFailed && _action.isAllowAbort()) {
                                    return;
                                }

                                cardsSelected(game, selectedCards);
                            }
                        }
                );
            }
        }
        else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), selectableCards, selectableCards, minimum, _maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            cardSelectionFailed = selectedCards.size() < _minimum;
                            if (cardSelectionFailed && _action.isAllowAbort()) {
                                return;
                            }

                            cardsSelected(game, selectedCards);
                        }
                    }
            );
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !cardSelectionFailed;
    }

    /**
     * This method is called when cards have been selected.
     * @param game the game
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
