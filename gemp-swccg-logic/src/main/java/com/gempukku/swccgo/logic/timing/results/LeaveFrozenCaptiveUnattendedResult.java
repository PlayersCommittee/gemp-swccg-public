package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a 'frozen' captive is left 'unattended'.
 */
public class LeaveFrozenCaptiveUnattendedResult extends EffectResult {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _site;

    /**
     * Creates an effect result that is emitted when a 'frozen' captive is left 'unattended'.
     * @param escort the escort
     * @param captive the captive
     * @param site the site
     */
    public LeaveFrozenCaptiveUnattendedResult(PhysicalCard escort, PhysicalCard captive, PhysicalCard site) {
        super(Type.LEFT_FROZEN_CAPTIVE_UNATTENDED, escort.getOwner());
        _escort = escort;
        _captive = captive;
        _site = site;
    }

    /**
     * Gets the escort that left the 'frozen' captive 'unattended'.
     * @return the escort
     */
    public PhysicalCard getEscort() {
        return _escort;
    }

    /**
     * Gets the 'frozen' captive that was left 'unattended'.
     * @return the captive
     */
    public PhysicalCard getCaptive() {
        return _captive;
    }

    /**
     * Gets the site the 'frozen' captive was left unattended at.
     * @return the site
     */
    public PhysicalCard getSite() {
        return _site;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_captive) + " left 'unattended'";
    }
}
