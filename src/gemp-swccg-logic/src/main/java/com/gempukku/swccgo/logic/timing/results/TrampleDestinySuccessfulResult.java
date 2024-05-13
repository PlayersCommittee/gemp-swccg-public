package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is emitted when a destiny draw for Trample is successful.
 */
public class TrampleDestinySuccessfulResult extends EffectResult {

    /**
     * Creates an effect that is emitted when a destiny draw for Trample is successful.
     * @param playerId the player whose destiny draw was successful
     */
    public TrampleDestinySuccessfulResult(String playerId) {
        super(Type.TRAMPLE_DESTINY_SUCCESSFUL, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Trample destiny draw successful";
    }
}