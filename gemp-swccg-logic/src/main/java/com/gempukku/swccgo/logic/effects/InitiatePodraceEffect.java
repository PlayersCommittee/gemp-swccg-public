package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PodraceInitiatedResult;

/**
 * An effect to initiate a Podrace.
 */
public class InitiatePodraceEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that initiates a Podrace.
     * @param action the action performing this effect
     */
    public InitiatePodraceEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        gameState.sendMessage(_action.getPerformingPlayer() + " initiates a Podrace");
        gameState.setPodraceStarted(_action.getActionAttachedToCard());
        game.getActionsEnvironment().emitEffectResult(new PodraceInitiatedResult(_action));
    }
}
