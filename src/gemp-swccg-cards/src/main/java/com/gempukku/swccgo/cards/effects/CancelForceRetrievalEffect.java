package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceRetrievalState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the current Force retrieval.
 */
public class CancelForceRetrievalEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that cancels the current Force retrieval.
     * @param action the action performing this effect
     */
    public CancelForceRetrievalEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ForceRetrievalState forceRetrievalState = gameState.getTopForceRetrievalState();
        if (forceRetrievalState != null && forceRetrievalState.canContinue()) {
            if (forceRetrievalState.getForceRetrievalEffect().mayNotBeCanceled()) {
                gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " may not cancel " + forceRetrievalState.getForceRetrievalEffect().getPlayerToRetrieveForce() + "'s Force retrieval");
            } else {
                forceRetrievalState.cancel();
                gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " cancels " + forceRetrievalState.getForceRetrievalEffect().getPlayerToRetrieveForce() + "'s Force retrieval");
            }
        }
    }
}
