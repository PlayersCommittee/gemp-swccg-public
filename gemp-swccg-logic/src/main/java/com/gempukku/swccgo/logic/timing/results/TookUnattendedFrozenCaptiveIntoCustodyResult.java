package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an 'unattended frozen' captive is taken into custody from a site.
 */
public class TookUnattendedFrozenCaptiveIntoCustodyResult extends EffectResult {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _site;

    /**
     * Creates an effect result that is emitted when an 'unattended frozen' captive is taken into custody from a site.
     * @param escort the escort
     * @param captive the captive
     * @param site the site
     */
    public TookUnattendedFrozenCaptiveIntoCustodyResult(PhysicalCard escort, PhysicalCard captive, PhysicalCard site) {
        super(Type.TOOK_UNATTENDED_FROZEN_CAPTIVE_INTO_CUSTODY, escort.getOwner());
        _escort = escort;
        _captive = captive;
        _site = site;
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
     * Gets the site the captive was taken into custody from.
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
        return GameUtils.getCardLink(_captive) + " taken into custody";
    }
}
