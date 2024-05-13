package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LightsaberCombatCanceledResult;

/**
 * An effect that cancels the current lightsaber combat.
 */
public class CancelLightsaberCombatEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that cancels the current lightsaber combat.
     * @param action the action performing this effect
     */
    public CancelLightsaberCombatEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
        if (lightsaberCombatState !=null && lightsaberCombatState.canContinue(game)) {
            gameState.sendMessage(_action.getPerformingPlayer() + " cancels lightsaber combat using " + GameUtils.getCardLink(_action.getActionSource()));
            lightsaberCombatState.cancel();
            game.getActionsEnvironment().emitEffectResult(new LightsaberCombatCanceledResult(_action.getPerformingPlayer()));
        }
    }
}
