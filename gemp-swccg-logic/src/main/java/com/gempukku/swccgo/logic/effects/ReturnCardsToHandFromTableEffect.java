package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
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
 * An effect that causes the specified cards on table to be returned to hand.
 */
public class ReturnCardsToHandFromTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private Zone _playersAttachedCardsGoToZone;
    private Zone _opponentsAttachedCardsGoToZone;

    /**
     * Creates an effect that causes the specified cards on table to be returned to hand.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public ReturnCardsToHandFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        this(action, cards, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be returned to hand.
     * @param action the action performing this effect
     * @param cards the cards
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public ReturnCardsToHandFromTableEffect(Action action, Collection<PhysicalCard> cards, Zone attachedCardsGoToZone) {
        this(action, cards, attachedCardsGoToZone, attachedCardsGoToZone);
    }

    /**
     * Creates an effect that causes the specified cards on table to be returned to hand.
     * @param action the action performing this effect
     * @param cards the cards
     * @param playersAttachedCardsGoToZone the zone that any of player's attached cards go to (instead of Lost Pile)
     * @param opponentsAttachedCardsGoToZone the zone that any of opponent's attached cards go to (instead of Lost Pile)
     */
    public ReturnCardsToHandFromTableEffect(Action action, Collection<PhysicalCard> cards, Zone playersAttachedCardsGoToZone, Zone opponentsAttachedCardsGoToZone) {
        super(action);
        _cards.addAll(cards);
        _playersAttachedCardsGoToZone = playersAttachedCardsGoToZone;
        _opponentsAttachedCardsGoToZone = opponentsAttachedCardsGoToZone;
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
                    new ChooseNextCardToReturnToHand(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to return to hand from table.
     */
    private class ChooseNextCardToReturnToHand extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to return to hand from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToReturnToHand(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to return to hand", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out returning card to hand from table
                SubAction returnCardsSubAction = new SubAction(_subAction);
                returnCardsSubAction.appendEffect(
                        new ReturnCardsToHandFromTableSimultaneouslyEffect(returnCardsSubAction, selectedCards, true, _playersAttachedCardsGoToZone, _opponentsAttachedCardsGoToZone));
                // Stack sub-action
                _subAction.stackSubAction(returnCardsSubAction);

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
                                                new ChooseNextCardToReturnToHand(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
