package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that allows the player to peek at and reorder the top cards in the specified card pile.
 */
abstract class PeekAtAndReorderTopCardsOfCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private String _cardPileOwner;
    private List<PhysicalCard> _cardsInOldOrder = new LinkedList<PhysicalCard>();
    private List<PhysicalCard> _remainingCardsToReorder = new LinkedList<PhysicalCard>();
    private List<PhysicalCard> _cardsInNewOrder = new LinkedList<PhysicalCard>();
    private int _numCards;

    /**
     * Create an effect that allows the player to peek at and reorder the top cards in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player examining and reordering the top cards in the card pile
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param numCards the number of cards to peek at and reorder
     */
    protected PeekAtAndReorderTopCardsOfCardPileEffect(Action action, String playerId, Zone cardPile, String cardPileOwner, int numCards) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
        _numCards = numCards;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final int numCardsToPeekAt = Math.min(_numCards, gameState.getCardPileSize(_cardPileOwner, _cardPile));
                        if (numCardsToPeekAt == 0) {
                            return;
                        }

                        gameState.sendMessage(_playerId + " peeks at and reorders the top " + numCardsToPeekAt + " card" + GameUtils.s(numCardsToPeekAt) + " of " + _cardPileOwner + "'s " + _cardPile.getHumanReadable());
                        _cardsInOldOrder.addAll(gameState.getCardPile(_cardPileOwner, _cardPile, false));

                        for (PhysicalCard card : _cardsInOldOrder) {
                            if (!card.isInserted()) {
                                _remainingCardsToReorder.add(card);
                                if (_remainingCardsToReorder.size() >= numCardsToPeekAt) {
                                    break;
                                }
                            }
                        }

                        subAction.appendEffect(
                                new ChooseNextCardToPutInPile(subAction));

                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Put cards rest of the cards into the new order
                                        for (PhysicalCard card : _cardsInOldOrder) {
                                            if (!_cardsInNewOrder.contains(card)
                                                    && !card.isInserted()) {
                                                _cardsInNewOrder.add(card);
                                            }
                                        }
                                        for (int i=0; i<_cardsInOldOrder.size(); ++i) {
                                            PhysicalCard card = _cardsInOldOrder.get(i);
                                            if (!_cardsInNewOrder.contains(card)
                                                    && card.isInserted()) {
                                                _cardsInNewOrder.add(i, card);
                                            }
                                        }
                                        // Put cards in card pile in new order
                                        for (int i=_cardsInOldOrder.size()-1; i>=0; --i) {
                                            PhysicalCard card = _cardsInOldOrder.get(i);
                                            boolean lastCard = i==0;
                                            // if not the last card, skip listener updates
                                            gameState.removeCardFromZone(card, !lastCard, !lastCard);
                                        }
                                        for (PhysicalCard card : _cardsInNewOrder) {
                                            boolean lastCard = card.equals(_cardsInNewOrder.get(_cardsInNewOrder.size()-1));
                                            // if not the last card, skip listener updates
                                            gameState.addCardToZone(card, _cardPile, _cardPileOwner, !lastCard, !lastCard);
                                        }
                                        gameState.sendMessage(_playerId + " has completed peeking at and reordering the top " + numCardsToPeekAt + " card" + GameUtils.s(numCardsToPeekAt) + " of " + _cardPileOwner + "'s " + _cardPile.getHumanReadable());
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    private class ChooseNextCardToPutInPile extends ChooseArbitraryCardsEffect {
        private SubAction _subAction;

        public ChooseNextCardToPutInPile(SubAction subAction) {
            super(subAction, _playerId, "Choose next card to put on " + _cardPileOwner + "'s " +  _cardPile.getHumanReadable(), _remainingCardsToReorder, Filters.any, 1, 1, false);
            _subAction = subAction;
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                _cardsInNewOrder.add(0, selectedCard);
                _remainingCardsToReorder.remove(selectedCard);
                if (!_remainingCardsToReorder.isEmpty()) {
                    _subAction.insertEffect(
                            new ChooseNextCardToPutInPile(_subAction));
                }
            }
        }
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
