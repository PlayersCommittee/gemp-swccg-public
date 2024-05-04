package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the current weapon targeting.
 */
public class CancelWeaponTargetingEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that the current weapon targeting.
     * @param action the action performing this effect
     */
    public CancelWeaponTargetingEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            RespondableWeaponFiringEffect respondableWeaponFiringEffect = (RespondableWeaponFiringEffect) weaponFiringState.getWeaponFiringEffect();
            if (!respondableWeaponFiringEffect.isCanceled()) {
                respondableWeaponFiringEffect.cancel(_action.getActionSource());
                gameState.sendMessage(_action.getPerformingPlayer() + " cancels weapon targeting using " + GameUtils.getCardLink(_action.getActionSource()));
            }
        }
    }
}
