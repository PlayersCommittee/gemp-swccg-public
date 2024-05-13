package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when an attack is canceled.
 */
public class CancelAttackResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when an attack is canceled.
     * @param playerId the player that canceled the attack
     */
    public CancelAttackResult(String playerId) {
        super(Type.ATTACK_CANCELED, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Attack just canceled";
    }
}
