package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.List;

/**
 * An effect to put random cards from hand into the specified card pile.
 */
class PutRandomCardsFromHandInCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private String _handOwner;
    private int _downToSize;
    private int _amount;
    private boolean _bottom;
    private Zone _zone;
    private boolean _hidden;
    private int _putInCardPileSoFar;
    private boolean _completed;
    private PutRandomCardsFromHandInCardPileEffect _that;
    private boolean _checkedRemoveTwoMore;
    private boolean _checkedRemoveThreeMore;


    /**
     * Creates an effect that causes the player to put cards at random from specified player's hand into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     * @param downToSize the minimum number of cards the hand can get down to from this effect
     * @param amount the number of cards to put on card pile (without going below downToSize)
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     */
    protected PutRandomCardsFromHandInCardPileEffect(Action action, String playerId, String handOwner, int downToSize, int amount, Zone cardPile, boolean bottom) {
        super(action);
        _playerId = playerId;
        _handOwner = handOwner;
        _downToSize = downToSize;
        _amount = amount;
        _zone = cardPile;
        _bottom = bottom;
        _hidden = true;
        _that = this;
        _checkedRemoveTwoMore = false;
        _checkedRemoveThreeMore = false;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getGameState().getHand(_handOwner).size() > _downToSize;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final PhysicalCard actionSource = _action.getActionSource();
        final String performingPlayer = _action.getPerformingPlayer();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_handOwner, _zone)
                    || (gameState.getCardPile(_handOwner, _zone).size() > 1 && _bottom);
        }

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String opponent = game.getOpponent(_handOwner);
                        String actionSourceOwner = actionSource != null ? actionSource.getOwner() : null;
                        if ((opponent.equals(performingPlayer) || opponent.equals(actionSourceOwner)) && modifiersQuerying.mayNotRemoveCardsFromOpponentsHand(gameState, actionSource, opponent)) {
                            gameState.sendMessage(opponent + " is not allowed to remove cards from " + _handOwner + "'s hand");
                            return;
                        }
                        subAction.appendEffect(getPutOneRandomCardInCardPileEffect(subAction));
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getPutOneRandomCardInCardPileEffect(final SubAction subAction) {
        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(final SwccgGame game) {
                if (!_checkedRemoveTwoMore
                        && game.getModifiersQuerying().hasGameTextModification(game.getGameState(), _action.getActionSource(), ModifyGameTextType.REMOVE_TWO_MORE_CARDS)) {
                    _checkedRemoveTwoMore = true;
                    _downToSize -= 2;
                }
                if (!_checkedRemoveThreeMore
                        && game.getModifiersQuerying().hasGameTextModification(game.getGameState(), _action.getActionSource(), ModifyGameTextType.REMOVE_THREE_MORE_CARDS)) {
                    _checkedRemoveThreeMore = true;
                    _downToSize -= 3;
                }
                if (_that.isPlayableInFull(game)) {
                    List<PhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getHand(_handOwner), 1);
                    if (!randomCards.isEmpty()) {
                        final PhysicalCard card = randomCards.get(0);
                        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(card);
                        String whereInPile = _bottom ? "bottom of " : "";
                        String msgText = _playerId + " puts " + cardInfo + " at random from " + (_playerId.equals(_handOwner) ? "" : (_handOwner + "'s")) + " hand on " + whereInPile + _zone.getHumanReadable();
                        subAction.appendEffect(
                                new PutOneCardFromHandInCardPileEffect(subAction, card, _zone, _handOwner, _bottom, msgText) {
                                    @Override
                                    protected void scheduleNextStep() {
                                        _putInCardPileSoFar++;
                                        if (_putInCardPileSoFar < _amount
                                                && _that.isPlayableInFull(game)) {
                                            subAction.appendEffect(
                                                    getPutOneRandomCardInCardPileEffect(subAction));
                                        }
                                        _completed = true;
                                    }
                                });
                    }
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _completed;
    }
}
