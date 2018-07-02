package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a battle is canceled.
 */
public class CancelBattleResult extends EffectResult {
    private PhysicalCard _location;

    /**
     * Creates an effect result that is emitted when a battle is canceled.
     * @param playerId the player that canceled the battle
     * @param location the battle location
     */
    public CancelBattleResult(String playerId, PhysicalCard location) {
        super(Type.BATTLE_CANCELED, playerId);
        _location = location;
    }

    /**
     * Gets the battle location.
     * @return the battle location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle at " + GameUtils.getCardLink(_location) + " just canceled";
    }
}
