package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ChooseToStealAndAttachOrBeLostEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;


/**
 * An effect that causes the specified player to choose cards on table to steal as attached or be lost.
 */
public class ChooseCardsToStealAndAttachOrBeLostEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _minimum;
    private int _maximum;
    private Filterable _cardFilter;
    private PhysicalCard _attachTo;

    /**
     * Creates an effect that causes the specified player to choose cards on table to steal as attached or be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards
     * @param maximum the maximum number of cards
     * @param cardFilter the card filter
     * @param attachTo the card to attach the stolen cards to
     */
    public ChooseCardsToStealAndAttachOrBeLostEffect(Action action, String playerId, int minimum, int maximum, Filterable cardFilter, PhysicalCard attachTo) {
        super(action);
        _performingPlayerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _cardFilter = cardFilter;
        _attachTo = attachTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new ChooseCardsOnTableEffect(subAction, _performingPlayerId, "Choose card" + GameUtils.s(_maximum) + " to steal or be lost", _minimum, _maximum, _cardFilter) {
                    @Override
                    protected void cardsSelected(final Collection<PhysicalCard> targetedCards) {
                        // Perform result(s)
                        subAction.appendEffect(
                                new ChooseNextCardToStealAndAttachOrBeLost(subAction, game, targetedCards));
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card.
     */
    private class ChooseNextCardToStealAndAttachOrBeLost extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card.
         * @param subAction the action
         * @param remainingCards the remaining cards
         */
        public ChooseNextCardToStealAndAttachOrBeLost(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose next card to steal or be lost", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                _game.getGameState().cardAffectsCard(_performingPlayerId, _action.getActionSource(), selectedCard);

                // SubAction to carry out choose card to be stolen or lost from table
                SubAction moveAwayOrBeLostSubAction = new SubAction(_subAction);
                moveAwayOrBeLostSubAction.appendEffect(
                        new ChooseToStealAndAttachOrBeLostEffect(moveAwayOrBeLostSubAction, selectedCard.getOwner(), selectedCard, _attachTo));
                // Stack sub-action
                _subAction.stackSubAction(moveAwayOrBeLostSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, _cardFilter);

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextCardToStealAndAttachOrBeLost(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
