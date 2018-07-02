package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawCardFromForcePileResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect to draws the top (or bottom) cards into hand from the specified card pile.
 */
abstract class DrawCardsIntoHandFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _amount;
    private Zone _zone;
    private boolean _reshuffle;
    private boolean _hidden;
    private boolean _bottom;
    private boolean _isTakeIntoHand;
    private Collection<PhysicalCard> _cardsDrawnIntoHand = new ArrayList<PhysicalCard>();
    private DrawCardsIntoHandFromPileEffect _that;

    /**
     * Creates an effect that causes the player to draw the top cards into hand from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to draw into hand
     * @param zone the card pile to draw cards from
     * @param bottom true if cards are drawn from the bottom, otherwise cards are drawn from the top
     * @param isTakeIntoHand true if take into hand, false if draw into hand
     */
    protected DrawCardsIntoHandFromPileEffect(Action action, String playerId, int amount, Zone zone, boolean bottom, boolean isTakeIntoHand) {
        super(action);
        _playerId = playerId;
        _amount = amount;
        _zone = zone;
        _reshuffle = false;
        _hidden = true;
        _bottom = bottom;
        _isTakeIntoHand = isTakeIntoHand;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getCardPile(_playerId, _zone).isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_playerId, _zone)
                    || (gameState.getCardPile(_playerId, _zone).size() > 1 && _bottom);
        }

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getDrawOneIntoHandEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Shuffle the card pile
                        if (_reshuffle) {
                            subAction.insertEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _playerId, _playerId, _zone, true));
                        }
                        else if (!_cardsDrawnIntoHand.isEmpty()) {
                            actionsEnvironment.emitEffectResult(
                                    new RemovedFromCardPileResult(subAction));
                        }
                    }
                }
        );
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Only callback with the cards still in the player's hand
                        cardsDrawnIntoHand(Filters.filter(_cardsDrawnIntoHand, game, Filters.inHand(_playerId)));
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getDrawOneIntoHandEffect(final SubAction subAction) {
        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (_zone == Zone.FORCE_PILE && !_isTakeIntoHand) {
                    game.getActionsEnvironment().emitEffectResult(new AboutToDrawCardFromForcePileResult(_playerId));
                }
                subAction.insertEffect(
                        new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(final SwccgGame game) {
                                final PhysicalCard card = _bottom ? game.getGameState().getBottomOfCardPile(_playerId, _zone) : game.getGameState().getTopOfCardPile(_playerId, _zone);
                                if (card != null) {
                                    String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(card);
                                    String zoneInfo = (_bottom ? "bottom of " : "") + _zone.getHumanReadable();
                                    String msgText = _playerId + " draws " + cardInfo + " into hand from " + zoneInfo;
                                    subAction.insertEffect(
                                            new TakeOneCardIntoHandFromOffTableEffect(subAction, _playerId, card, msgText) {
                                                @Override
                                                protected void afterCardTakenIntoHand() {
                                                    _cardsDrawnIntoHand.add(card);
                                                    if (_cardsDrawnIntoHand.size() < _amount
                                                            && _that.isPlayableInFull(game)) {
                                                        subAction.insertEffect(
                                                                getDrawOneIntoHandEffect(subAction));
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                );
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardsDrawnIntoHand.size() >= _amount;
    }

    protected abstract void cardsDrawnIntoHand(Collection<PhysicalCard> cards);
}
