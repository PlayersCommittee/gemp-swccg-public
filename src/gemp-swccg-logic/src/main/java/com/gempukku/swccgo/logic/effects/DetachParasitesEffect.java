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
 * An effect that causes the specified parasites to be detached.
 */
public class DetachParasitesEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private DetachParasitesEffect _that;

    /**
     * Creates an effect that causes the specified parasites to be detached.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public DetachParasitesEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _cards.addAll(cards);
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId != null ? _performingPlayerId : game.getGameState().getCurrentPlayerId());

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Filter for cards that are still on the table and attached to a card
                        _cards = Filters.filter(_cards, game, Filters.and(Filters.onTable, Filters.attachedTo(Filters.any)));

                        if (!_cards.isEmpty()) {
                            subAction.appendEffect(
                                    new ChooseNextParasiteToDetach(subAction, game, _cards));
                        }
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
     * A private effect for choosing the next parasite to detach
     */
    private class ChooseNextParasiteToDetach extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to lose from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to detach
         */
        public ChooseNextParasiteToDetach(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to detach", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out detaching parasite
                SubAction loseCardsSubAction = new SubAction(_subAction);
                loseCardsSubAction.appendEffect(
                        new DetachParasiteEffect(_action, selectedCard));
                // Stack sub-action
                _subAction.stackSubAction(loseCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table and attached to a card
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.onTable, Filters.attachedTo(Filters.any)));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextParasiteToDetach(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
