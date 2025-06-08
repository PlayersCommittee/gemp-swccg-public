package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.TransferredCaptiveToNewEscortResult;

/**
 * The effect to transfer an escorted captive to a new escort.
 */
public class TransferEscortedCaptiveToNewEscortEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _captive;
    private PhysicalCard _newEscort;

    /**
     * Create an effect to transfer an escorted captive to a new escort.
     * @param action the action performing this effect
     * @param captive the captive
     * @param newEscort the new escort
     */
    public TransferEscortedCaptiveToNewEscortEffect(Action action, PhysicalCard captive, PhysicalCard newEscort) {
        super(action);
        _captive = captive;
        _newEscort = newEscort;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard oldEscort = _captive.getAttachedTo();

        gameState.sendMessage(GameUtils.getCardLink(_captive) + " is transferred from " + GameUtils.getCardLink(oldEscort) + " to " + GameUtils.getCardLink(_newEscort));
        gameState.seizeCharacter(game, _captive, _newEscort);

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new TransferredCaptiveToNewEscortResult(_captive, oldEscort, _newEscort));
    }
}
