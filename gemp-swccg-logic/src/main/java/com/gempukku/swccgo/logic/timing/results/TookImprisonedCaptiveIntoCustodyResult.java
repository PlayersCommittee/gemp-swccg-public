package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an imprisoned captive is taken into custody from a prison.
 */
public class TookImprisonedCaptiveIntoCustodyResult extends EffectResult {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _prison;

    /**
     * Creates an effect result that is emitted when an imprisoned captive is taken into custody from a prison.
     * @param escort the escort
     * @param captive the captive
     * @param prison the prison
     */
    public TookImprisonedCaptiveIntoCustodyResult(PhysicalCard escort, PhysicalCard captive, PhysicalCard prison) {
        super(Type.TOOK_IMPRISONED_CAPTIVE_INTO_CUSTODY, escort.getOwner());
        _escort = escort;
        _captive = captive;
        _prison = prison;
    }

    /**
     * Gets the escort that took the captive into custody.
     * @return the escort
     */
    public PhysicalCard getEscort() {
        return _escort;
    }

    /**
     * Gets the captive that was taken into custody.
     * @return the captive
     */
    public PhysicalCard getCaptive() {
        return _captive;
    }

    /**
     * Gets the prison the captive was taken into custody from.
     * @return the prison
     */
    public PhysicalCard getPrison() {
        return _prison;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_captive) + " taken into custody from " + GameUtils.getCardLink(_prison);
    }
}
