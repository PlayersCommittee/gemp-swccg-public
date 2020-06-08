package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An effect that causes the specified cards in hand to be forfeited.
 */
class ForfeitCardsFromHandEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified cards in hand to be forfeited.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public ForfeitCardsFromHandEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _cards.addAll(cards);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.inHand(_action.getPerformingPlayer()));
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Filter for cards that are still in hand
        _cards = Filters.filter(_cards, game, Filters.inHand(_action.getPerformingPlayer()));

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToForfeit(subAction, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to forfeit from hand.
     */
    private class ChooseNextCardToForfeit extends ChooseCardsFromHandEffect {
        private SubAction _subAction;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to forfeit from hand.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToForfeit(SubAction subAction, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), 1, 1, Filters.in(remainingCards));
            _subAction = subAction;
            _remainingCards = remainingCards;
        }

        @Override
        public String getChoiceText(int numCardsToChoose) {
            return "Choose card to forfeit";
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // Check if card is forfeited to Used Pile
                boolean goesToUsedPile = game.getModifiersQuerying().isForfeitedToUsedPile(game.getGameState(), selectedCard);

                // SubAction to carry out forfeiting card from table
                SubAction forfeitCardsSubAction = new SubAction(_subAction);
                forfeitCardsSubAction.appendEffect(
                        new ForfeitCardsFromOffTableSimultaneouslyEffect(forfeitCardsSubAction, Collections.singleton(selectedCard), goesToUsedPile, false));
                // Stack sub-action
                _subAction.stackSubAction(forfeitCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still in hand
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.inHand(_action.getPerformingPlayer()));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ForfeitCardsFromHandEffect.ChooseNextCardToForfeit(_subAction, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
