package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ForceDrainCompletedResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

/**
 * An effect that performs a Force drain.
 */
public class ForceDrainEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;
    private boolean _carriedOut;

    /**
     * Creates an effect that performs a Force drain at a location.
     * @param action the action performing this effect
     * @param location the location
     */
    public ForceDrainEffect(Action action, PhysicalCard location) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Record that Force drain is initiated (and cards involved)
        subAction.appendEffect(
                new RecordForceDrainInitiatedEffect(subAction, _location));

        // 2) Automatic and optional responses to Force drain being initiated
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new ForceDrainInitiatedResult(subAction, _location)));

        // 3) Calculate the Force drain amount and perform the Force loss
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if Force drain is not canceled
                        if (gameState.getForceDrainState().canContinue()) {

                            // Record actual amount Force drained
                            float amountToDrain = modifiersQuerying.getForceDrainAmount(gameState, _location, _performingPlayerId);
                            game.getGameState().sendMessage(_performingPlayerId + " Force drains " + GuiUtils.formatAsString(amountToDrain) + " at " + GameUtils.getCardLink(_location));
                            game.getModifiersQuerying().forceDrainPerformed(_location, amountToDrain);
                            _carriedOut = true;

                            // Perform Force loss
                            SubAction forceLossSubAction = new SubAction(subAction);
                            forceLossSubAction.appendEffect(
                                    new LoseForceEffect(subAction, game.getOpponent(_performingPlayerId), amountToDrain, false, true, false, false, false, false, null, false, false));
                            subAction.stackSubAction(forceLossSubAction);
                        }
                    }
                });

        // 4) Force drain is setFulfilledByOtherAction
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if Force drain is not canceled
                        if (gameState.getForceDrainState().canContinue()) {

                            // Automatic and optional responses from Force drain setFulfilledByOtherAction
                            game.getActionsEnvironment().emitEffectResult(new ForceDrainCompletedResult(subAction, _location));
                        }

                        game.getGameState().endForceDrain();
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _carriedOut;
    }
}
