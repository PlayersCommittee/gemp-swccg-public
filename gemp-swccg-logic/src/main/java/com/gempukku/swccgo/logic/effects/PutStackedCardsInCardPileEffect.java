package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An effect to put stacked cards into the specified card pile.
 */
class PutStackedCardsInCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _stackedCards;
    private Collection<PhysicalCard> _remainingCards = new ArrayList<PhysicalCard>();
    private int _minimum;
    private int _maximum;
    private PhysicalCard _stackedOn;
    private Filterable _stackedOnFilters;
    private Filterable _filters;
    private Zone _cardPile;
    private boolean _bottom;
    private boolean _hidden;
    private int _putInCardPileSoFar;
    private PutStackedCardsInCardPileEffect _that;

    /**
     * Creates an effect that causes the player to put cards stacked on the specified card in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put in card pile
     * @param maximum the maximum number of cards to put in card pile
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param stackedOn the card that the stacked cards are stacked on
     */
    protected PutStackedCardsInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, boolean bottom, PhysicalCard stackedOn) {
        this(action, playerId, minimum, maximum, cardPile, bottom, stackedOn, Filters.any);
        _hidden = true;
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter that are stacked on the specified
     * card in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put in card pile
     * @param maximum the maximum number of cards to put in card pile
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param stackedOn the card that the stacked cards are stacked on
     * @param filters the filter
     */
    protected PutStackedCardsInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, boolean bottom, PhysicalCard stackedOn, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _cardPile = cardPile;
        _bottom = bottom;
        _stackedOn = stackedOn;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put cards stacked on a card accepted by the specified stackedOn filter
     * in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put in card pile
     * @param maximum the maximum number of cards to put in card pile
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param stackedOnFilters the stackedOn filter
     */
    protected PutStackedCardsInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, boolean bottom, Filterable stackedOnFilters) {
        this(action, playerId, minimum, maximum, cardPile, bottom, stackedOnFilters, Filters.any);
        _hidden = true;
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter that are stacked on the specified
     * card in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put in card pile
     * @param maximum the maximum number of cards to put in card pile
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    protected PutStackedCardsInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, boolean bottom, Filterable stackedOnFilters, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _bottom = bottom;
        _cardPile = cardPile;
        _stackedOnFilters = stackedOnFilters;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put specified stacked cards in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCards the stacked cards
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param hidden true if cards are not revealed when put in pile, otherwise false
     */
    protected PutStackedCardsInCardPileEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, Zone cardPile, boolean bottom, boolean hidden) {
        super(action);
        _playerId = playerId;
        _stackedCards = stackedCards;
        _bottom = bottom;
        _cardPile = cardPile;
        _hidden = hidden;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !game.getGameState().isCardPileFaceUp(_playerId, _cardPile)
                    || (game.getGameState().getCardPile(_playerId, _cardPile).size() > 1 && _bottom);
        }

        SubAction subAction = new SubAction(_action);
        if (_stackedCards != null) {
            _remainingCards.addAll(_stackedCards);
            if (!_remainingCards.isEmpty()) {
                subAction.appendEffect(
                        new ChooseAndPutNextCardInCardPile(subAction, _playerId, _remainingCards));
            }
        }
        else {
            subAction.appendEffect(getChooseOneStackedCardToPutInCardPileEffect(subAction));
        }
        return subAction;
    }

    private StandardEffect getChooseOneStackedCardToPutInCardPileEffect(final SubAction subAction) {
        if (_stackedOn != null) {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOn, _putInCardPileSoFar < _minimum ? 1 : 0, 1, _filters) {
                @Override
                public String getChoiceText(int numCardsToChoose) {
                    String whereInPile = _bottom ? "bottom of " : "";
                    return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on " + whereInPile + _cardPile.getHumanReadable();
                }
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        _that.cardsSelected(subAction, game, cards.iterator().next());
                    }
                }
            };
        }
        else {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOnFilters, _putInCardPileSoFar < _minimum ? 1 : 0, 1, _filters) {
                @Override
                public String getChoiceText(int numCardsToChoose) {
                    String whereInPile = _bottom ? "bottom of " : "";
                    return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on " + whereInPile + _cardPile.getHumanReadable();
                }
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        _that.cardsSelected(subAction, game, cards.iterator().next());
                    }
                }
            };
        }
    }

    private void cardsSelected(final SubAction subAction, final SwccgGame game, PhysicalCard card) {
        String cardInfo = (_hidden && card.getZone().isFaceDown()) ? "a card" : GameUtils.getCardLink(card);
        String whereInPile = _bottom ? "bottom of " : "";
        String msgText = _playerId + " puts " + cardInfo + " on " + whereInPile + _cardPile.getHumanReadable() + " from " + GameUtils.getCardLink(card.getStackedOn());
        subAction.appendEffect(
                new PutOneStackedCardInCardPileEffect(subAction, card, _cardPile, _bottom, msgText) {
                    @Override
                    protected void scheduleNextStep() {
                        _putInCardPileSoFar++;
                        if (_putInCardPileSoFar < _maximum
                                && _that.isPlayableInFull(game)) {
                            subAction.appendEffect(
                                    getChooseOneStackedCardToPutInCardPileEffect(subAction));
                        }
                    }
                });
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _putInCardPileSoFar >= _minimum;
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
         *
         * @param subAction      the action
         * @param playerId       the player
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseAndPutNextCardInCardPile(SubAction subAction, String playerId, Collection<PhysicalCard> remainingCards) {
            super(subAction, playerId, (_bottom ? "Choose card to put on bottom of " : "Choose card to put on ") + _cardPile.getHumanReadable(), remainingCards, 1, 1);
            _subAction = subAction;
            _playerId = playerId;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                game.getGameState().removeCardsFromZone(Collections.singletonList(selectedCard));
                String cardInfo = (_hidden && _cardPile.isFaceDown()) ? "a card" : GameUtils.getCardLink(selectedCard);
                String playerToPutCardInPile = _playerId != null ? _playerId : selectedCard.getOwner();
                String cardPileText = selectedCard.getOwner().equals(playerToPutCardInPile) ? _cardPile.getHumanReadable() : (selectedCard.getOwner() + "'s " + _cardPile.getHumanReadable());

                if (_bottom) {
                    game.getGameState().addCardToZone(selectedCard, _cardPile, selectedCard.getOwner());
                    game.getGameState().sendMessage(playerToPutCardInPile + " puts " + cardInfo + " on bottom of " + cardPileText);
                } else {
                    game.getGameState().addCardToTopOfZone(selectedCard, _cardPile, selectedCard.getOwner());
                    game.getGameState().sendMessage(playerToPutCardInPile + " puts " + cardInfo + " on " + cardPileText);
                }

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty())
                    _subAction.appendEffect(
                            new ChooseAndPutNextCardInCardPile(_subAction, _playerId, _remainingCards));
            }
        }
    }
}
