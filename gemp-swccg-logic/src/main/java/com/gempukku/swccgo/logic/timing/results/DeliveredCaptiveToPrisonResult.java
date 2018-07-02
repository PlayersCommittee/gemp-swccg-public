package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a captive is delivered to a prison.
 */
public class DeliveredCaptiveToPrisonResult extends EffectResult {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private float _forfeitValue;
    private PhysicalCard _prison;

    /**
     * Creates an effect result that is emitted when an escorted captive is delivered to a prison.
     * @param escort the escort
     * @param captive the captive
     * @param forfeitValue the forfeit value of the captive prior to being delivered
     * @param prison the prison
     */
    public DeliveredCaptiveToPrisonResult(PhysicalCard escort, PhysicalCard captive, float forfeitValue, PhysicalCard prison) {
        super(Type.DELIVERED_CAPTIVE_TO_PRISON, escort.getOwner());
        _escort = escort;
        _captive = captive;
        _forfeitValue = forfeitValue;
        _prison = prison;
    }

    /**
     * Gets the escort that delivered the captive.
     * @return the escort
     */
    public PhysicalCard getEscort() {
        return _escort;
    }

    /**
     * Gets the captive that was delivered.
     * @return the captive
     */
    public PhysicalCard getCaptive() {
        return _captive;
    }

    /**
     * Gets the forfeit value of the captive when being delivered.
     * @return the forfeit value
     */
    public float getForfeitValue() {
        return _forfeitValue;
    }

    /**
     * Gets the prison the captive was delivered to.
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
        return "Delivered " + GameUtils.getCardLink(_captive) + " to " + GameUtils.getCardLink(_prison);
    }
}
