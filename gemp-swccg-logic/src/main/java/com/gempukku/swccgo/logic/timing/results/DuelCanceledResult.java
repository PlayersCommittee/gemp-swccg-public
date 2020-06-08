package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a duel is canceled.
 */
public class DuelCanceledResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when a duel is canceled.
     * @param playerId the player that canceled the duel.
     */
    public DuelCanceledResult(String playerId) {
        super(Type.DUEL_CANCELED, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Duel just canceled";
    }
}
