package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a 'damaged' Podracer is 'repaired'.
 */
public class RepairedPodracerResult extends EffectResult {
    private PhysicalCard _podracer;

    /**
     * Creates an effect result that is emitted when a 'damaged' Podracer is 'repaired'.
     * @param performingPlayerId the performing player
     * @param podracer the Podracer
     */
    public RepairedPodracerResult(String performingPlayerId, PhysicalCard podracer) {
        super(Type.PODRACER_REPAIRED, performingPlayerId);
        _podracer = podracer;
    }

    /**
     * Gets the Podracer.
     * @return the Podracer
     */
    public PhysicalCard getPodracer() {
        return _podracer;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_podracer) + " just 'repaired'";
    }
}
