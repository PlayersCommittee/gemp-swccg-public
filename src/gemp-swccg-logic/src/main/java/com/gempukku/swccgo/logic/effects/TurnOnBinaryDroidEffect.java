package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.BinaryTurnedOnResult;

/**
 * An effect to turn on (a binary droid).
 */
public class TurnOnBinaryDroidEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _binaryDroid;

    /**
     * Creates an effect that turns on (a binary droid).
     * @param action the action performing this effect
     * @param binaryDroid the binary droid that is turned on
     */
    public TurnOnBinaryDroidEffect(Action action, PhysicalCard binaryDroid) {
        super(action);
        _binaryDroid = binaryDroid;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (modifiersQuerying.cannotTurnOnBinaryDroid(gameState, _binaryDroid))
            return;

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_binaryDroid) + " is turned on");
        if (source.getCardId() != _binaryDroid.getCardId()) {
            gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _binaryDroid);
        }
        gameState.turnOnBinaryDroid(_binaryDroid);

        actionsEnvironment.emitEffectResult(new BinaryTurnedOnResult(_action.getPerformingPlayer(), _binaryDroid));
    }
}
