package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Force drain is enhanced by a weapon.
 */
public class EnhanceForceDrainResult extends EffectResult {
    private PhysicalCard _weapon;
    private SwccgBuiltInCardBlueprint _permanentWeapon;

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
     * Creates an effect result that is triggered when a Force drain is enhanced by a permanent weapon.
     * @param playerId the player enhancing the Force drain
     * @param permanentWeapon the permanent weapon enhancing the Force drain
     */
    public EnhanceForceDrainResult(String playerId, SwccgBuiltInCardBlueprint permanentWeapon) {
        super(Type.FORCE_DRAIN_ENHANCED_BY_WEAPON, playerId);
        _permanentWeapon = permanentWeapon;
    }

    /**
     * Gets the weapon card enhancing the Force drain, or null when a permanent weapon enhanced it.
     * @return the weapon card, or null
     */
    public PhysicalCard getWeapon() {
        return _weapon;
    }

    /**
     * Gets the permanent weapon enhancing the Force drain, or null when a weapon card enhanced it.
     * @return the permanent weapon, or null
     */
    public SwccgBuiltInCardBlueprint getPermanentWeapon() {
        return _permanentWeapon;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        if (_weapon != null) {
            return "Force drain enhanced by " + GameUtils.getCardLink(_weapon);
        }
        if (_permanentWeapon != null) {
            return "Force drain enhanced by " + _permanentWeapon.getTitle(game);
        }
        return "Force drain enhanced by a weapon";
    }
}
