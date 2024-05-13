package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a squadron replaces starfighters.
 */
public class SquadronReplacementResult extends EffectResult {
    private PhysicalCard _squadron;

    /**
     * Creates an effect result that is emitted when a squadron replaces starfighters.
     * @param playerId the performing player
     * @param squadron the squadron
     */
    public SquadronReplacementResult(String playerId, PhysicalCard squadron) {
        super(Type.SQUADRON_REPLACEMENT, playerId);
        _squadron = squadron;
    }

    /**
     * Gets the squadron.
     * @return the squadron
     */
    public PhysicalCard getSquadron() {
        return _squadron;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_squadron) + " just replaced starfighters";
    }
}
