package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a location is converted.
 */
public class ConvertLocationResult extends EffectResult {
    private PhysicalCard _oldLocation;
    private PhysicalCard _newLocation;

    /**
     * Creates an effect result that is emitted when a location is converted by the player.
     * @param playerId the performing player
     * @param oldLocation the old location
     * @param newLocation the new location
     */
    public ConvertLocationResult(String playerId, PhysicalCard oldLocation, PhysicalCard newLocation) {
        super(EffectResult.Type.CONVERT_LOCATION, playerId);
        _oldLocation = oldLocation;
        _newLocation = newLocation;
    }

    /**
     * Gets the old location.
     * @return the old location
     */
    public PhysicalCard getOldLocation() {
        return _oldLocation;
    }

    /**
     * Gets the new location.
     * @return the new location
     */
    public PhysicalCard getNewLocation() {
        return _newLocation;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_oldLocation) + " just converted to " + GameUtils.getCardLink(_newLocation);
    }
}
