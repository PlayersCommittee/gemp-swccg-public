package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.BinaryTurnedOffResult;

/**
 * An effect to turn off (a binary droid).
 */
public class TurnOffBinaryDroidEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _binaryDroid;

    /**
     * Creates an effect that turns off (a binary droid).
     * @param action the action performing this effect
     * @param binaryDroid the binary droid that is turned off
     */
    public TurnOffBinaryDroidEffect(Action action, PhysicalCard binaryDroid) {
        super(action);
        _binaryDroid = binaryDroid;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_binaryDroid) + " is turned off");
        if (source.getCardId() != _binaryDroid.getCardId()) {
            gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _binaryDroid);
        }
        gameState.turnOffBinaryDroid(_binaryDroid);

        actionsEnvironment.emitEffectResult(new BinaryTurnedOffResult(_action.getPerformingPlayer(), _binaryDroid));
    }
}
