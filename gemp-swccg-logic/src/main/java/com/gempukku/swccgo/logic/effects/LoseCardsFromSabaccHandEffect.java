package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromSabaccHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards in sabacc hand to be lost.
 */
class LoseCardsFromSabaccHandEffect extends AbstractSubActionEffect {
    private String _handOwner;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private boolean _toBottomOfPile;

    /**
     * Creates an effect that causes the specified cards in sabacc hand to be lost.
     * @param action the action performing this effect
     * @param handOwner the sabacc hand owner
     * @param cards the cards
     */
    protected LoseCardsFromSabaccHandEffect(Action action, String handOwner, Collection<PhysicalCard> cards) {
        this(action, handOwner, cards, false);
    }

    /**
     * Creates an effect that causes the specified cards in sabacc hand to be lost.
     * @param action the action performing this effect
     * @param handOwner the sabacc hand owner
     * @param cards the cards
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    protected LoseCardsFromSabaccHandEffect(Action action, String handOwner, Collection<PhysicalCard> cards, boolean toBottomOfPile) {
        super(action);
        _handOwner = handOwner;
        _cards.addAll(cards);
        _toBottomOfPile = toBottomOfPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.inSabaccHand(_handOwner));
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.inSabaccHand(_handOwner));

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToLose(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to lose from sabacc hand.
     */
    private class ChooseNextCardToLose extends ChooseCardsFromSabaccHandEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to lose from sabacc hand.
         * @param subAction the action
         * @param remainingCards the remaining cards to lose
         */
        public ChooseNextCardToLose(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), _handOwner, 1, 1, Filters.in(remainingCards));
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        public String getChoiceText(int numCardsToChoose) {
            return "Choose next card to lose";
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out placing card in card pile from table
                SubAction placeCardsSubAction = new SubAction(_subAction);
                placeCardsSubAction.appendEffect(
                        new LoseCardsFromOffTableSimultaneouslyEffect(placeCardsSubAction, selectedCards, _toBottomOfPile));
                // Stack sub-action
                _subAction.stackSubAction(placeCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still in sabacc hand
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.inSabaccHand(_handOwner));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new LoseCardsFromSabaccHandEffect.ChooseNextCardToLose(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
