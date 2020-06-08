package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedUsingHyperspeedResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingHyperspeedResult;

import java.util.Collection;
import java.util.Collections;


/**
 * An effect to move a starship using hyperspeed.
 */
public class MoveStarshipUsingHyperspeedEffect extends AbstractSubActionEffect implements MovingAsReactEffect, PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;
    private boolean _asMoveAway;
    private boolean _moveCompleted;
    private PhysicalCard _preventedCard;
    private MoveStarshipUsingHyperspeedEffect _that;

    /**
     * Creates an effect to move a starship using hyperspeed.
     * @param action the action performing this effect
     * @param cardMoved the starship to move
     * @param moveTo the system to move to
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     */
    public MoveStarshipUsingHyperspeedEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo, boolean asReact, boolean asMoveAway) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _movedFrom = cardMoved.getAtLocation();
        _movedTo = moveTo;
        _asReact = asReact;
        _asMoveAway = asMoveAway;
        _that = this;
    }

    @Override
    public PhysicalCard getMovingFrom() {
        return _movedFrom;
    }

    @Override
    public Collection<PhysicalCard> getCardsMoving() {
        return Collections.singletonList(_cardMoved);
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCard = card;
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return card.equals(_preventedCard);
    }

    @Override
    public Effect.Type getType() {
        return _asReact ? Type.MOVING_AS_REACT_USING_HYPERSPEED : null;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Moving ");
        if (_asMoveAway) {
            msgText.append("away ");
        }
        msgText.append(GameUtils.getCardLink(_cardMoved))
                .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo))
                .append(" using hyperspeed");
        if (_asReact) {
            msgText.append(" as a 'react'");
        }
        return msgText.toString();
    }

    public boolean isAsReact() {
        return _asReact;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);

        // Check if card is still at a location and 'react' was not canceled
        if (_cardMoved.getAtLocation() != null
                && (!isAsReact() || gameState.getMoveAsReactState().canContinue())) {

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Record that regular move was performed
                            game.getModifiersQuerying().regularMovePerformed(_cardMoved);
                        }
                    }
            );

            // Emit effect result that card is beginning to move
            subAction.appendEffect(
                    new TriggeringResultEffect(subAction,
                            new MovingUsingHyperspeedResult(_cardMoved, _playerId, _movedFrom, _movedTo, _asReact, _asMoveAway, _that)));

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // Check that card is at a location and may still move
                            if (_cardMoved.getAtLocation() == null) {
                                return;
                            }
                            if (_cardMoved.equals(_preventedCard)) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)) {
                                return;
                            }

                            // Move card and send message
                            gameState.moveCardToLocation(_cardMoved, _movedTo);
                            StringBuilder msgText = new StringBuilder(_playerId).append(" moves ");
                            if (_asMoveAway) {
                                msgText.append("away ");
                            }
                            msgText.append(GameUtils.getCardLink(_cardMoved))
                                    .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo))
                                    .append(" using hyperspeed");
                            if (isAsReact()) {
                                msgText.append(" as a 'react'");
                            }
                            gameState.sendMessage(msgText.toString());
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new MovedUsingHyperspeedResult(_cardMoved, _playerId, _movedFrom, _movedTo, _asReact));
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _moveCompleted;
    }
}
