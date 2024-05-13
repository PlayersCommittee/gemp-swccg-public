package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the current targeting.
 */
public class CancelTargetingEffect extends AbstractSuccessfulEffect {
    private RespondableEffect _respondableEffect;

    /**
     * Creates an effect that the current targeting.
     * @param action the action performing this effect
     * @param respondableEffect the effect performing the targeting
     */
    public CancelTargetingEffect(Action action, RespondableEffect respondableEffect) {
        super(action);
        _respondableEffect = respondableEffect;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (!_respondableEffect.isCanceled()) {
            _respondableEffect.cancel(_action.getActionSource());
            gameState.sendMessage(_action.getPerformingPlayer() + " cancels targeting using " + GameUtils.getCardLink(_action.getActionSource()));
        }
    }
}
