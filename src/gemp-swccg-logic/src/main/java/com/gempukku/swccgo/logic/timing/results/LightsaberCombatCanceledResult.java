package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a lightsaber combat is canceled.
 */
public class LightsaberCombatCanceledResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when a lightsaber combat is canceled.
     * @param playerId the player that canceled the lightsaber combat.
     */
    public LightsaberCombatCanceledResult(String playerId) {
        super(Type.LIGHTSABER_COMBAT_CANCELED, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Lightsaber combat just canceled";
    }
}
