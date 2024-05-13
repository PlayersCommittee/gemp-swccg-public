package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an Effect of any kind on table is retargeted.
 */
public class RetargetedEffectResult extends EffectResult {
    private PhysicalCard _effectRetargeted;

    /**
     * Creates an effect result that is emitted when an Effect of any kind on table is retargeted.
     * @param effectRetargeted the Effect that was re-targeted
     * @param playerId the performing player
     */
    public RetargetedEffectResult(PhysicalCard effectRetargeted, String playerId) {
        super(Type.RETARGETED_EFFECT, playerId);
        _effectRetargeted = effectRetargeted;
    }

    /**
     * Gets the Effect of any kind on table that was re-targeted.
     * @return the Effect the was re-targeted
     */
    public PhysicalCard getEffectRetargeted() {
        return _effectRetargeted;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Re-targeted " + GameUtils.getCardLink(_effectRetargeted);
    }
}
