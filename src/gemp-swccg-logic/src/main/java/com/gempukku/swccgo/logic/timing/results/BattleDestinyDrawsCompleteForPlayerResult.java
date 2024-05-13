package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when battle destiny draws are complete for the specified player.
 */
public class BattleDestinyDrawsCompleteForPlayerResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when battle destiny draws are complete for the specified player.
     * @param playerId the player whose battle destiny drawing is complete
     */
    public BattleDestinyDrawsCompleteForPlayerResult(String playerId) {
        super(Type.BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle destiny draws complete for " + getPerformingPlayerId();
    }
}
