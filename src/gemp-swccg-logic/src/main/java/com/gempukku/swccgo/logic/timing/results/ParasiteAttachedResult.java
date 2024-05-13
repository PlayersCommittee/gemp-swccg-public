package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a parasite creature is attached to a host.
 */
public class ParasiteAttachedResult extends EffectResult {
    private PhysicalCard _creature;
    private PhysicalCard _host;

    /**
     * Creates an effect result that is emitted when a parasite creature is attached to a host.
     * @param performingPlayerId the performing player
     * @param creature the creature
     * @param host the card the creature was attached to
     */
    public ParasiteAttachedResult(String performingPlayerId, PhysicalCard creature, PhysicalCard host) {
        super(Type.PARASITE_ATTACHED, performingPlayerId);
        _creature = creature;
        _host = host;
    }

    public PhysicalCard getParasite() {
        return _creature;
    }

    public PhysicalCard getHost() {
        return _host;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_creature) + " just attached to " + GameUtils.getCardLink(_host);
    }
}
