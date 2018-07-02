package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be canceled.
 */
public class CancelCardsOnTableEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified cards on table to be canceled.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public CancelCardsOnTableEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _cards.addAll(cards);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action, _performingPlayerId != null ? _performingPlayerId : game.getGameState().getCurrentPlayerId());

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.onTable);

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToCancel(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card on table to cancel.
     */
    private class ChooseNextCardToCancel extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card on table to cancel.
         * @param subAction the action
         * @param remainingCards the remaining cards to cancel
         */
        public ChooseNextCardToCancel(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to cancel", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out canceling card on table
                SubAction cancelCardsSubAction = new SubAction(_subAction);
                cancelCardsSubAction.appendEffect(
                        new CancelCardsOnTableSimultaneouslyEffect(cancelCardsSubAction, selectedCards));
                // Stack sub-action
                _subAction.stackSubAction(cancelCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.onTable);

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextCardToCancel(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
