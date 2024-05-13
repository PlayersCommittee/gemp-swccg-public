package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a battle when the weapons segment is just setFulfilledByOtherAction.
 */
public class BattleWeaponsSegmentCompletedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during a battle when the weapons segment is just complete.
     * @param performingPlayerId the player that initiated battle
     */
    public BattleWeaponsSegmentCompletedResult(String performingPlayerId) {
        super(Type.BATTLE_WEAPONS_SEGMENT_COMPLETED, performingPlayerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just after battle weapons segment";
    }
}
