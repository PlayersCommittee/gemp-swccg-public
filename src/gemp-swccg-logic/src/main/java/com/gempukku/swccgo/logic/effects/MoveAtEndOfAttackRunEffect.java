package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedAtEndOfAttackRunResult;
import com.gempukku.swccgo.logic.timing.results.MovingAtEndOfAttackRunResult;

/**
 * An effect for a starfighter to move at end of Attack Run.
 */
public class MoveAtEndOfAttackRunEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _moveCompleted;

    /**
     * Creates an effect for a starfighter to move at end of Attack Run.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the location to move to
     */
    public MoveAtEndOfAttackRunEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _movedFrom = cardMoved.getAtLocation();
        _movedTo = moveTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // Emit effect result that card is beginning to move
        subAction.appendEffect(
                new TriggeringResultEffect(subAction,
                        new MovingAtEndOfAttackRunResult(_cardMoved, _playerId, _movedFrom, _movedTo)));

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
                        StringBuilder msgText = new StringBuilder(_playerId).append(" moves ").append(GameUtils.getCardLink(_cardMoved))
                                .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo))
                                .append(" at end of an Attack Run");
                        gameState.sendMessage(msgText.toString());
                        _moveCompleted = true;

                        // Emit effect result
                        game.getActionsEnvironment().emitEffectResult(new MovedAtEndOfAttackRunResult(_cardMoved, _playerId, _movedFrom, _movedTo));
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _moveCompleted;
    }
}
