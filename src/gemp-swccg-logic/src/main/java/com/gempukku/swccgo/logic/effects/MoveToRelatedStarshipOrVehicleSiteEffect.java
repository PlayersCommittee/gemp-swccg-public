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
import com.gempukku.swccgo.logic.timing.results.MoveToRelatedStarshipOrVehicleSiteResult;
import com.gempukku.swccgo.logic.timing.results.MovingToRelatedStarshipOrVehicleSiteResult;

/**
 * An effect to move to a starship/vehicle site from a related starship/vehicle.
 */
public class MoveToRelatedStarshipOrVehicleSiteEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private boolean _fromCargoHold;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _moveCompleted;

    /**
     * Creates an effect to move to a starship/vehicle site from a related starship/vehicle.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the starship/vehicle to move to
     */
    public MoveToRelatedStarshipOrVehicleSiteEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _fromCargoHold = cardMoved.isInCargoHoldAsVehicle() || cardMoved.isInCargoHoldAsStarfighterOrTIE();
        _movedFrom = cardMoved.getAttachedTo();
        _movedTo = moveTo;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Moving ").append(GameUtils.getCardLink(_cardMoved)).append(" from ");
        if (_fromCargoHold) {
            msgText.append("cargo hold of ");
        }
        msgText.append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
        return msgText.toString();
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

        // Check if card is still attached
        if (_cardMoved.getAttachedTo() != null) {

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
                            new MovingToRelatedStarshipOrVehicleSiteResult(_cardMoved, _playerId, _movedFrom, _movedTo)));

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // Check that card is still attached and may still move
                            if (_cardMoved.getAttachedTo() == null) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)) {
                                return;
                            }

                            StringBuilder msgText = new StringBuilder(_cardMoved.getOwner()).append(" moves ").append(GameUtils.getCardLink(_cardMoved)).append(" from ");
                            if (_fromCargoHold) {
                                msgText.append("cargo hold of ");
                            }
                            msgText.append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
                            gameState.sendMessage(msgText.toString());
                            game.getGameState().moveCardToLocation(_cardMoved, _movedTo);
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new MoveToRelatedStarshipOrVehicleSiteResult(_cardMoved, _playerId, _movedFrom, _movedTo));
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
