package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Force drain is enhanced by a weapon.
 */
public class EnhanceForceDrainResult extends EffectResult {
    private PhysicalCard _weapon;

    /**
     * Creates an effect result that is triggered when a Force drain is enhanced by a weapon.
     * @param playerId the player enhancing the Force drain
     * @param weapon the weapon enhancing the Force drain
     */
    public EnhanceForceDrainResult(String playerId, PhysicalCard weapon) {
        super(Type.FORCE_DRAIN_ENHANCED_BY_WEAPON, playerId);
        _weapon = weapon;
    }

    /**
     * Gets the weapon enhancing the Force drain
     * @return the weapon
     */
    public PhysicalCard getWeapon() {
        return _weapon;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Force drain enhanced by " + GameUtils.getCardLink(_weapon);
    }
}
