package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.TakingOffResult;
import com.gempukku.swccgo.logic.timing.results.TookOffResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect for a starship or vehicle to take off.
 */
public class TakeOffEffect extends AbstractSubActionEffect implements MovingAsReactEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;
    private boolean _asUnlimitedMove;
    private boolean _moveCompleted;

    /**
     * Creates an effect for a starship or vehicle to take off.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the location to move to
     * @param asReact true if moving as a 'react', otherwise false
     */
    public TakeOffEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo, boolean asReact, boolean asUnlimitedMove) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _movedFrom = cardMoved.getAtLocation();
        _movedTo = moveTo;
        _asReact = asReact;
        _asUnlimitedMove = asUnlimitedMove;
    }

    @Override
    public PhysicalCard getMovingFrom() {
        return _movedFrom;
    }

    @Override
    public Collection<PhysicalCard> getCardsMoving() {
        return Collections.singletonList(_cardMoved);
    }

    @Override
    public Type getType() {
        return _asReact ? Type.TAKING_OFF_AS_REACT : null;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Taking ").append(GameUtils.getCardLink(_cardMoved))
                .append(" off from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
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

            if (!_asUnlimitedMove
                    && !modifiersQuerying.takesOffAsUnlimitedMove(gameState, _cardMoved)) {
                subAction.appendEffect(
                        new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                // Record that regular move was performed
                                game.getModifiersQuerying().regularMovePerformed(_cardMoved);
                            }
                        }
                );
            }

            // Emit effect result that card is beginning to move
            subAction.appendEffect(
                    new TriggeringResultEffect(subAction,
                            new TakingOffResult(_cardMoved, _playerId, _movedFrom, _movedTo, _asReact)));

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // Check that card is at a location and may still move
                            if (_cardMoved.getAtLocation() == null) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)) {
                                return;
                            }

                            // Move card and send message
                            gameState.moveCardToLocation(_cardMoved, _movedTo);
                            StringBuilder msgText = new StringBuilder(_playerId).append(" takes ").append(GameUtils.getCardLink(_cardMoved))
                                    .append(" off from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
                            if (isAsReact()) {
                                msgText.append(" as a 'react'");
                            }
                            gameState.sendMessage(msgText.toString());
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new TookOffResult(_cardMoved, _playerId, _movedFrom, _movedTo, _asReact));
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
