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
import java.util.Collections;

/**
 * An effect that causes the specified cards on table to be forfeited.
 */
class ForfeitCardsFromTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified cards on table to be forfeited.
     * @param action the action performing this effect
     * @param cards the cards
     */
    protected ForfeitCardsFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _cards.addAll(cards);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.onTable);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.onTable);

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToForfeit(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to forfeit from table.
     */
    private class ChooseNextCardToForfeit extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to forfeit from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToForfeit(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to forfeit", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // Check if card is forfeited to Used Pile
                boolean goesToUsedPile = _game.getModifiersQuerying().isForfeitedToUsedPile(_game.getGameState(), selectedCard);

                // SubAction to carry out forfeiting card from table
                SubAction forfeitCardsSubAction = new SubAction(_subAction);
                forfeitCardsSubAction.appendEffect(
                        new ForfeitCardsFromTableSimultaneouslyEffect(forfeitCardsSubAction, Collections.singleton(selectedCard), null, goesToUsedPile));
                // Stack sub-action
                _subAction.stackSubAction(forfeitCardsSubAction);

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
                                                new ForfeitCardsFromTableEffect.ChooseNextCardToForfeit(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
