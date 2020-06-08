package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a parasite creature is detached from a host.
 */
public class ParasiteDetachedResult extends EffectResult {
    private PhysicalCard _creature;
    private PhysicalCard _host;

    /**
     * Creates an effect result that is emitted when a parasite creature is detached from a host.
     * @param performingPlayerId the performing player
     * @param creature the creature
     * @param host the card the creature was attached to
     */
    public ParasiteDetachedResult(String performingPlayerId, PhysicalCard creature, PhysicalCard host) {
        super(Type.PARASITE_DETACHED, performingPlayerId);
        _creature = creature;
        _host = host;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_creature) + " just detached from " + GameUtils.getCardLink(_host);
    }
}
