package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromSabaccHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StealOneCardIntoCardPileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards in sabacc hand to be placed in the specified card pile.
 */
class PlaceCardsInCardPileFromSabaccHandEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private String _handOwner;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private Zone _cardPile;
    private String _cardPileOwner;
    private boolean _toBottomOfPile;

    /**
     * Creates an effect that causes the specified cards in sabacc hand to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param performingPlayerId the player
     * @param handOwner the sabacc hand owner
     * @param cards the cards
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     */
    protected PlaceCardsInCardPileFromSabaccHandEffect(Action action, String performingPlayerId, String handOwner, Collection<PhysicalCard> cards, Zone cardPile, String cardPileOwner) {
        this(action, performingPlayerId, handOwner, cards, cardPile, cardPileOwner, false);
    }

    /**
     * Creates an effect that causes the specified cards in sabacc hand to be placed in the specified card pile.
     * @param action the action performing this effect
     * @param performingPlayerId the player
     * @param handOwner the sabacc hand owner
     * @param cards the cards
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    protected PlaceCardsInCardPileFromSabaccHandEffect(Action action, String performingPlayerId, String handOwner, Collection<PhysicalCard> cards, Zone cardPile, String cardPileOwner, boolean toBottomOfPile) {
        super(action);
        _performingPlayerId = performingPlayerId;
        _handOwner = handOwner;
        _cards.addAll(cards);
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
        _toBottomOfPile = toBottomOfPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.inSabaccHand(_handOwner));
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action, _performingPlayerId);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.inSabaccHand(_handOwner));

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
     * A private effect for choosing the next card to place in card pile from sabacc hand.
     */
    private class ChooseNextCardToPlaceInCardPile extends ChooseCardsFromSabaccHandEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to place in card pile from sabacc hand.
         * @param subAction the action
         * @param game the game
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToPlaceInCardPile(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), _handOwner, 1, 1, Filters.in(remainingCards));
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        public String getChoiceText(int numCardsToChoose) {
            return "Choose card to place in " + _cardPile.getHumanReadable();
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out placing card in card pile from table
                SubAction placeCardsSubAction = new SubAction(_subAction);
                if (_handOwner.equals(_cardPileOwner)) {
                    placeCardsSubAction.appendEffect(
                            new PlaceCardsInCardPileFromOffTableSimultaneouslyEffect(placeCardsSubAction, selectedCards, _cardPile, _cardPileOwner, _toBottomOfPile));
                }
                else {
                    // This is when cards are 'stolen' as stakes won
                    placeCardsSubAction.appendEffect(
                            new StealOneCardIntoCardPileEffect(placeCardsSubAction, selectedCard, _cardPile));
                }
                // Stack sub-action
                _subAction.stackSubAction(placeCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.inSabaccHand(_handOwner));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new PlaceCardsInCardPileFromSabaccHandEffect.ChooseNextCardToPlaceInCardPile(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
