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
import java.util.List;

/**
 * An effect that causes the specified cards on table to be placed out of play.
 */
public class PlaceCardsOutOfPlayFromTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private List<PhysicalCard> _placedOutOfPlay = new ArrayList<PhysicalCard>();
    private PlaceCardsOutOfPlayFromTableEffect _that;

    /**
     * Creates an effect that causes the specified cards on table to be placed out of play.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public PlaceCardsOutOfPlayFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _cards.addAll(cards);
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.onTable);

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToPlaceOutOfPlay(subAction, game, _cards));
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            cardsPlacedOutOfPlay(_placedOutOfPlay);
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * Determines if the cards are lost due to being 'eaten'.
     * @return true or false
     */
    protected boolean asEaten() {
        return false;
    }

    /**
     * A private effect for choosing the next card to place out of play from table.
     */
    private class ChooseNextCardToPlaceOutOfPlay extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to place out of play from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToPlaceOutOfPlay(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to place out of play", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out placing card out of play from table
                SubAction placeCardsSubAction = new SubAction(_subAction);
                placeCardsSubAction.appendEffect(
                        new PlaceCardsOutOfPlayFromTableSimultaneouslyEffect(placeCardsSubAction, selectedCards, true) {
                            @Override
                            protected boolean asEaten() {
                                return _that.asEaten();
                            }
                        });
                // Stack sub-action
                _subAction.stackSubAction(placeCardsSubAction);

                _placedOutOfPlay.add(selectedCard);
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
                                                new ChooseNextCardToPlaceOutOfPlay(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }

    /**
     * A callback method for the cards placed out of play.
     * @param cards the cards placed out of play
     */
    protected void cardsPlacedOutOfPlay(List<PhysicalCard> cards) {
    }
}
