package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An effect that has the player's choose the order in which cards are placed in the specified card pile.
 */
class PutCardsInCardPileEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _remainingLightCards = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _remainingDarkCards = new ArrayList<PhysicalCard>();
    private Zone _cardPile;
    private String _playerToChooseOrder;
    private boolean _toBottomOfPile;

    /**
     * Creates an effect that has the players choose the order in which cards are placed in the specified card pile.
     * @param action the action performing this effect
     * @param game the game
     * @param cards the cards
     * @param cardPile the card pile
     */
    public PutCardsInCardPileEffect(Action action, SwccgGame game, Collection<PhysicalCard> cards, Zone cardPile) {
        this(action, game, cards, cardPile, false);
    }

    /**
     * Creates an effect that has the players choose the order in which cards are placed in the specified card pile.
     * @param action the action performing this effect
     * @param game the game
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PutCardsInCardPileEffect(Action action, SwccgGame game, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile) {
        this(action, game, cards, cardPile, null, toBottomOfPile);
    }

    /**
     * Creates an effect that has the players choose the order in which cards are placed in the specified card pile.
     * @param action the action performing this effect
     * @param game the game
     * @param cards the cards
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PutCardsInCardPileEffect(Action action, SwccgGame game, Collection<PhysicalCard> cards, Zone cardPile, boolean toBottomOfPile, boolean lostCardsShouldNotCountAsJustLost) {
        this(action, game, cards, cardPile, null, toBottomOfPile, lostCardsShouldNotCountAsJustLost);
    }

    /**
     * Creates an effect that has the players choose the order in which cards are placed in the specified card pile.
     * @param action the action performing this effect
     * @param game the game
     * @param cards the cards
     * @param cardPile the card pile
     * @param playerToChooseOrder the player to choose order for all cards placed in Lost Pile, null if owner's choose order
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PutCardsInCardPileEffect(Action action, SwccgGame game, Collection<PhysicalCard> cards, Zone cardPile, String playerToChooseOrder, boolean toBottomOfPile) {
        super(action);
        for (PhysicalCard card : cards) {
            if (card.getOwner().equals(game.getDarkPlayer()))
                _remainingDarkCards.add(card);
            else
                _remainingLightCards.add(card);
        }
        _cardPile = cardPile;
        _playerToChooseOrder = playerToChooseOrder;
        _toBottomOfPile = toBottomOfPile;
    }

    /**
     * Creates an effect that has the players choose the order in which cards are placed in the specified card pile.
     * @param action the action performing this effect
     * @param game the game
     * @param cards the cards
     * @param cardPile the card pile
     * @param playerToChooseOrder the player to choose order for all cards placed in Lost Pile, null if owner's choose order
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PutCardsInCardPileEffect(Action action, SwccgGame game, Collection<PhysicalCard> cards, Zone cardPile, String playerToChooseOrder, boolean toBottomOfPile, boolean lostCardsDoNotCountAsJustLost) {
        super(action);
        for (PhysicalCard card : cards) {
            if (card.getOwner().equals(game.getDarkPlayer()))
                _remainingDarkCards.add(card);
            else
                _remainingLightCards.add(card);
        }
        _cardPile = cardPile;
        _playerToChooseOrder = playerToChooseOrder;
        _toBottomOfPile = toBottomOfPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {

        SubAction subAction = new SubAction(_action);
        if (game.getGameState().getCurrentPlayerId().equals(game.getDarkPlayer()))
            if (!_remainingDarkCards.isEmpty())
                subAction.appendEffect(
                        new ChooseAndPutNextCardInCardPile(subAction, _playerToChooseOrder != null ? _playerToChooseOrder : game.getDarkPlayer(), _remainingDarkCards));
        if (!_remainingLightCards.isEmpty())
            subAction.appendEffect(
                    new ChooseAndPutNextCardInCardPile(subAction, _playerToChooseOrder != null ? _playerToChooseOrder : game.getLightPlayer(), _remainingLightCards));
        if (!game.getGameState().getCurrentPlayerId().equals(game.getDarkPlayer()))
            if (!_remainingDarkCards.isEmpty())
                subAction.appendEffect(
                        new ChooseAndPutNextCardInCardPile(subAction, _playerToChooseOrder != null ? _playerToChooseOrder : game.getDarkPlayer(), _remainingDarkCards));

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to place in the card pile.
     */
    private class ChooseAndPutNextCardInCardPile extends ChooseArbitraryCardsEffect {
        private Collection<PhysicalCard> _remainingCards;
        private SubAction _subAction;
        private String _playerId;

        /**
         * Creates an effect for choosing the next card to place in the card pile.
         * @param subAction the action
         * @param playerId the player
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseAndPutNextCardInCardPile(SubAction subAction, String playerId, Collection<PhysicalCard> remainingCards) {
            super(subAction, playerId, (_toBottomOfPile ? "Choose card to put on bottom of " : "Choose card to put on ") + _cardPile.getHumanReadable(), remainingCards, 1, 1);
            _subAction = subAction;
            _playerId = playerId;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                game.getGameState().removeCardsFromZone(Collections.singletonList(selectedCard));
                String cardPileText = selectedCard.getOwner().equals(_playerId) ? _cardPile.getHumanReadable() : (selectedCard.getOwner() + "'s " + _cardPile.getHumanReadable());

                if (_toBottomOfPile) {
                    game.getGameState().addCardToZone(selectedCard, _cardPile, selectedCard.getOwner());
                    game.getGameState().sendMessage(_playerId + " puts " + GameUtils.getCardLink(selectedCard) + " on bottom of " + cardPileText);
                }
                else {
                    game.getGameState().addCardToTopOfZone(selectedCard, _cardPile, selectedCard.getOwner());
                    game.getGameState().sendMessage(_playerId + " puts " + GameUtils.getCardLink(selectedCard) + " on " + cardPileText);
                }

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty())
                    _subAction.appendEffect(
                            new ChooseAndPutNextCardInCardPile(_subAction, _playerId, _remainingCards));
            }
        }
    }
}
