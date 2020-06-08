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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect to exchange cards from hand with bottom card from the specified card pile.
 */
public class ExchangeCardsInHandWithBottomCardInCardPileEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private final Zone _cardPile;
    private int _minimum;
    private int _maximum;
    private Filterable _cardsInHandFilter;
    private boolean _hidden;
    private List<PhysicalCard> _cardsToPlaceInCardPile = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect to exchange cards from hand with bottom card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     */
    protected ExchangeCardsInHandWithBottomCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _minimum = minimum;
        _maximum = maximum;
        _cardsInHandFilter = Filters.any;
        _hidden = true;
    }

    /**
     * Creates an effect to exchange cards from hand with bottom card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to exchange cards with
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     * @param cardsInHandFilter the cards in hand filter
     */
    protected ExchangeCardsInHandWithBottomCardInCardPileEffect(Action action, String playerId, Zone cardPile, int minimum, int maximum, Filterable cardsInHandFilter) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _minimum = minimum;
        _maximum = maximum;
        _cardsInHandFilter = cardsInHandFilter;
        _hidden = false;
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
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_playerId, _cardPile)
                    || (gameState.getCardPile(_playerId, _cardPile).size() > 1);
        }
        final PhysicalCard cardFromPile = gameState.getBottomOfCardPile(_playerId, _cardPile);
        final int indexOf = gameState.getCardPile(_playerId, _cardPile).indexOf(cardFromPile);

        final SubAction subAction = new SubAction(_action);
        if (indexOf >= 0) {
            subAction.appendEffect(
                    getChooseOneCardToPlaceInCardPileEffect(subAction));
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (!_cardsToPlaceInCardPile.isEmpty()) {
                                if (_hidden) {
                                    gameState.sendMessage(_playerId + " exchanges " + GameUtils.numCards(_cardsToPlaceInCardPile) + " from hand with bottom card in " + _cardPile.getHumanReadable());
                                }
                                else {
                                    gameState.sendMessage(_playerId + " exchanges " + GameUtils.getAppendedNames(_cardsToPlaceInCardPile) + " from hand with bottom card in " + _cardPile.getHumanReadable());
                                }
                                gameState.removeCardsFromZone(_cardsToPlaceInCardPile);
                                for (PhysicalCard cardToPlaceInPile : _cardsToPlaceInCardPile) {
                                    gameState.addCardToSpecificPositionInZone(cardToPlaceInPile, _cardPile, _playerId, indexOf);
                                }
                                gameState.removeCardFromZone(cardFromPile);
                                gameState.addCardToZone(cardFromPile, Zone.HAND, _playerId);

                                actionsEnvironment.emitEffectResult(
                                        new ExchangedCardsInCardPileResult(subAction));
                            }
                        }
                    }
            );
        }

        return subAction;
     }

    private StandardEffect getChooseOneCardToPlaceInCardPileEffect(final SubAction subAction) {
        return new ChooseCardsFromHandEffect(subAction, _playerId, _cardsToPlaceInCardPile.size() < _minimum ? 1 : 0, 1, _cardsInHandFilter) {
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
        return true;
    }
}
