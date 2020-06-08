package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when an Utinni Effect is completed.
 */
public class UtinniEffectCompletedResult extends EffectResult {
    private PhysicalCard _utinniEffect;

    /**
     * Creates an effect result that is triggered when an Utinni Effect is completed.
     * @param performingPlayerId the player that completed the Utinni Effect
     * @param utinniEffect the Objective
     */
    public UtinniEffectCompletedResult(String performingPlayerId, PhysicalCard utinniEffect) {
        super(Type.UTINNI_EFFECT_COMPLETED, performingPlayerId);
        _utinniEffect = utinniEffect;
    }

    /**
     * Gets the Utinni Effect that was completed.
     * @return the Utinni Effect
     */
    public PhysicalCard getUtinniEffect() {
        return _utinniEffect;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Utinni Effect, " + GameUtils.getCardLink(_utinniEffect) + ", is completed";
    }
}
