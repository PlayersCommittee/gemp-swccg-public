package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.ExchangedCardsInCardPileResult;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect to exchange cards from hand with a card from the specified card pile.
 */
public class ExchangeCardsInHandWithCardInCardPileEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private final Zone _cardPile;
    private int _minimum;
    private int _maximum;
    private Filterable _cardsInHandFilter;
    private Filterable _cardInPileFilter;
    private boolean _hiddenFromHand;
    private boolean _hiddenFromPile;
    private boolean _reshuffle;
    private List<PhysicalCard> _cardsToPlaceInCardPile = new ArrayList<PhysicalCard>();
    private boolean _exchangeCompleted;

    /**
     * Creates an effect to exchange cards from hand with a card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     */
    public ExchangeCardsInHandWithCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum, boolean reshuffle) {
        this(action, playerId, cardPile, minimum, maximum, reshuffle, false);
    }

    /**
     * Creates an effect to exchange cards from hand with a card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     * @param hiddenFromHand true if the card exchanged from hand should be hidden from opponent
     */
    public ExchangeCardsInHandWithCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum, boolean reshuffle, boolean hiddenFromHand) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _minimum = minimum;
        _maximum = maximum;
        _cardsInHandFilter = Filters.any;
        _cardInPileFilter = Filters.any;
        _hiddenFromHand = hiddenFromHand;
        _hiddenFromPile = true;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to exchange cards from hand with a card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     * @param cardsInHandFilter the cards in hand filter
     * @param cardInPileFilter the cards in pile filter
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     */
    protected ExchangeCardsInHandWithCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum, Filterable cardsInHandFilter, Filterable cardInPileFilter, boolean reshuffle) {
        this(action, playerId, cardPile, minimum, maximum, cardsInHandFilter, cardInPileFilter, reshuffle, false);
    }

    /**
     * Creates an effect to exchange cards from hand with a card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     * @param cardsInHandFilter the cards in hand filter
     * @param cardInPileFilter the cards in pile filter
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     * @param hiddenFromHand true if the card exchanged from hand should be hidden from opponent
     */
    protected ExchangeCardsInHandWithCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum, Filterable cardsInHandFilter, Filterable cardInPileFilter, boolean reshuffle, boolean hiddenFromHand) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _minimum = minimum;
        _maximum = maximum;
        _cardsInHandFilter = cardsInHandFilter;
        _cardInPileFilter = cardInPileFilter;
        _hiddenFromHand = hiddenFromHand;
        _hiddenFromPile = false;
        _reshuffle = reshuffle;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hiddenFromPile) {
            _hiddenFromPile = !gameState.isCardPileFaceUp(_playerId, _cardPile);
        }

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseCardFromPileEffect(subAction, _playerId, _cardPile, _playerId, _cardInPileFilter) {
                    @Override
                    protected void cardSelected(final SwccgGame game, final PhysicalCard cardFromPile) {
                        final int indexOf = gameState.getCardPile(_playerId, _cardPile).indexOf(cardFromPile);
                        if (indexOf >= 0) {
                            subAction.appendEffect(
                                    getChooseOneCardToPlaceInCardPileEffect(subAction));
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (_cardsToPlaceInCardPile.size() >= _minimum) {
                                                String toHandText = !_hiddenFromPile ? GameUtils.getCardLink(cardFromPile) : "a card";
                                                String toPileText = !_hiddenFromPile ? GameUtils.getAppendedNames(_cardsToPlaceInCardPile) : GameUtils.numCards(_cardsToPlaceInCardPile);
                                                if(_hiddenFromHand)
                                                    toPileText = GameUtils.numCards(_cardsToPlaceInCardPile);
                                                gameState.sendMessage(_playerId + " exchanges " + toPileText + " from hand with " + toHandText + " in " + _cardPile.getHumanReadable());

                                                gameState.removeCardsFromZone(_cardsToPlaceInCardPile);
                                                for (PhysicalCard cardToPlaceInPile : _cardsToPlaceInCardPile) {
                                                    gameState.addCardToSpecificPositionInZone(cardToPlaceInPile, _cardPile, _playerId, indexOf);
                                                }
                                                gameState.removeCardFromZone(cardFromPile);
                                                gameState.addCardToZone(cardFromPile, Zone.HAND, _playerId);
                                                _exchangeCompleted = true;
                                                
                                                if (_reshuffle) {
                                                    gameState.shufflePile(_playerId, _cardPile);
                                                    gameState.sendMessage(_playerId + " shuffles " + _cardPile.getHumanReadable());
                                                    actionsEnvironment.emitEffectResult(
                                                            new ShufflingResult(subAction.getActionSource(), _playerId, _playerId, _cardPile, true));
                                                }
                                                else {
                                                    actionsEnvironment.emitEffectResult(
                                                            new ExchangedCardsInCardPileResult(subAction));
                                                }
                                            }
                                        }
                                    }
                            );
                        }
                    }
                }
        );

        return subAction;
     }

    private StandardEffect getChooseOneCardToPlaceInCardPileEffect(final SubAction subAction) {
        return new ChooseCardsFromHandEffect(subAction, _playerId, _cardsToPlaceInCardPile.size() < _minimum ? 1 : 0, 1, Filters.and(_cardsInHandFilter, Filters.not(Filters.in(_cardsToPlaceInCardPile)))) {
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    _cardsToPlaceInCardPile.add(cards.iterator().next());
                    if (_cardsToPlaceInCardPile.size() < _maximum) {
                        subAction.insertEffect(
                                getChooseOneCardToPlaceInCardPileEffect(subAction));
                    }
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _exchangeCompleted;
    }
}
