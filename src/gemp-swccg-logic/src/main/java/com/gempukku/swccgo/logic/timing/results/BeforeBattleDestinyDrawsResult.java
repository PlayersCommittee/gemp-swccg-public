package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted just before battle destinies are drawn.
 */
public class BeforeBattleDestinyDrawsResult extends EffectResult {

    /**
     * Creates an effect result that is emitted just before battle destinies are drawn.
     * @param battleInitiatorPlayerId the player that initiated the battle
     */
    public BeforeBattleDestinyDrawsResult(String battleInitiatorPlayerId) {
        super(Type.BEFORE_BATTLE_DESTINY_DRAWS, battleInitiatorPlayerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Before either player draws battle destiny";
    }
}
