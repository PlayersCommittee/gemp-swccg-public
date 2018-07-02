package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when battle destiny draws are complete for both players.
 */
public class BattleDestinyDrawsCompleteForBothPlayersResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when battle destiny draws are complete for both players.
     * @param battleInitiatorPlayerId the player that initiated the battle
     */
    public BattleDestinyDrawsCompleteForBothPlayersResult(String battleInitiatorPlayerId) {
        super(Type.BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS, battleInitiatorPlayerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle destiny draws complete";
    }
}
