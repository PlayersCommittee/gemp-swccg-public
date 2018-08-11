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
 * An effect that causes the specified cards on table to be placed in the specified card pile.
 */
class PlaceCardsInCardPileFromTableEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private Zone _cardPile;
    private boolean _toBottomOfPile;
    private Zone _attachedCardsGoToZone;
    private boolean _allCardsSituation;

    /**
     * Creates an effect that causes the specified cards on table to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param cards the cards
     * @param cardPile the card pile
     */
    protected PlaceCardsInCardPileFromTableEffect(Action action, Collection<PhysicalCard> cards, Zone cardPile) {
        this(action, action.getPerformingPlayer(), cards, cardPile, false, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    protected PlaceCardsInCardPileFromTableEffect(Action action, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile) {
        this(action, action.getPerformingPlayer(), cards, cardPile, toBottomOfPile, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    protected PlaceCardsInCardPileFromTableEffect(Action action, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile, Zone attachedCardsGoToZone) {
        this(action, action.getPerformingPlayer(), cards, cardPile, toBottomOfPile, attachedCardsGoToZone);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player performing this action
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    protected PlaceCardsInCardPileFromTableEffect(Action action, String playerId, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile, Zone attachedCardsGoToZone) {
        super(action);
        _playerId = playerId;
        _cards.addAll(cards);
        _cardPile = cardPile;
        _toBottomOfPile = toBottomOfPile;
        _attachedCardsGoToZone = attachedCardsGoToZone;
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player performing this action
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     * @param allCardsSituation if is an all-cards situation
     */
    protected PlaceCardsInCardPileFromTableEffect(Action action, String playerId, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile, Zone attachedCardsGoToZone, boolean allCardsSituation) {
        super(action);
        _playerId = playerId;
        _cards.addAll(cards);
        _cardPile = cardPile;
        _toBottomOfPile = toBottomOfPile;
        _attachedCardsGoToZone = attachedCardsGoToZone;
        _allCardsSituation = allCardsSituation;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.onTable);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action, _playerId);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.onTable);

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToPlaceInCardPile(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to place in card pile from table.
     */
    private class ChooseNextCardToPlaceInCardPile extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to place in card pile from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToPlaceInCardPile(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to place on " + (_toBottomOfPile ? "bottom of " : "") + _cardPile.getHumanReadable(), 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out placing card in card pile from table
                SubAction placeCardsSubAction = new SubAction(_subAction);
                placeCardsSubAction.appendEffect(
                        new PlaceCardsInCardPileFromTableSimultaneouslyEffect(placeCardsSubAction, selectedCards, _cardPile, _toBottomOfPile, true, _attachedCardsGoToZone, false, false, false, null, false, true));
                // Stack sub-action
                _subAction.stackSubAction(placeCardsSubAction);

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
                                                new ChooseNextCardToPlaceInCardPile(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
