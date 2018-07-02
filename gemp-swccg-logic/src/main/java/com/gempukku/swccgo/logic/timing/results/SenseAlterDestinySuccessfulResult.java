package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is emitted when a destiny draw for Sense or Alter is successful.
 */
public class SenseAlterDestinySuccessfulResult extends EffectResult {

    /**
     * Creates an effect that is emitted when a destiny draw for Sense or Alter is successful.
     * @param playerId the player whose destiny draw was successful
     */
    public SenseAlterDestinySuccessfulResult(String playerId) {
        super(Type.SENSE_ALTER_DESTINY_SUCCESSFUL, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Sense or Alter destiny draw successful";
    }
}
