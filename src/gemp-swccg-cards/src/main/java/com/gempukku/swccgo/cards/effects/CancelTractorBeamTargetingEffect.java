package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.UsingTractorBeamState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RespondableUsingTractorBeamEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the current tractor beam targeting.
 */
public class CancelTractorBeamTargetingEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that the current tractor beam targeting.
     * @param action the action performing this effect
     */
    public CancelTractorBeamTargetingEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        UsingTractorBeamState usingTractorBeamState = gameState.getUsingTractorBeamState();
        if (usingTractorBeamState != null) {
            RespondableUsingTractorBeamEffect respondableUsingTractorBeamEffect = (RespondableUsingTractorBeamEffect) usingTractorBeamState.getTractorBeamEffect();
            if (!respondableUsingTractorBeamEffect.isCanceled()) {
                respondableUsingTractorBeamEffect.cancel(_action.getActionSource());
                gameState.sendMessage(_action.getPerformingPlayer() + " cancels tractor beam targeting using " + GameUtils.getCardLink(_action.getActionSource()));
            }
        }
    }
}
