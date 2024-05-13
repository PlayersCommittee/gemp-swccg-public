package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a system is 'probed'.
 */
public class ProbedSystemResult extends EffectResult {
    private PhysicalCard _systemProbed;

    /**
     * Creates an effect result that is emitted when a system is 'probed'.
     * @param playerId the player performing the 'probe'
     * @param systemProbed the system 'probed'
     */
    public ProbedSystemResult(String playerId, PhysicalCard systemProbed) {
        super(Type.PROBED_SYSTEM, playerId);
        _systemProbed = systemProbed;
    }

    /**
     * Gets the system that was 'probed'.
     * @return the system
     */
    public PhysicalCard getSystemProbed() {
        return _systemProbed;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_systemProbed) + " just 'probed'";
    }
}
