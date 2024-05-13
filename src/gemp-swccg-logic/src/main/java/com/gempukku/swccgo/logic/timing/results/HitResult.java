package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is 'hit'.
 */
public class HitResult extends EffectResult {
    private PhysicalCard _cardHit;
    private PhysicalCard _hitByCard;
    private SwccgBuiltInCardBlueprint _hitByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;

    /**
     * Creates an effect result that is emitted when a card is 'hit'.
     * @param cardHit the card hit
     * @param hitByCard the card that performed the hit
     * @param hitByPermanentWeapon the permanent weapon that performed the hit, or null
     * @param cardFiringWeapon the card that fired the weapon that performed the hit, or null
     */
    public HitResult(PhysicalCard cardHit, PhysicalCard hitByCard, SwccgBuiltInCardBlueprint hitByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(Type.HIT, hitByCard.getOwner());
        _cardHit = cardHit;
        _hitByCard = hitByCard;
        _hitByPermanentWeapon = hitByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
    }

    /**
     * Gets the card that was 'hit'.
     * @return the card that was 'hit'
     */
    public PhysicalCard getCardHit() {
        return _cardHit;
    }

    /**
     * Gets the card that performed the hit.
     * @return the card that performed the hit
     */
    public PhysicalCard getHitByCard() {
        return _hitByCard;
    }

    /**
     * Gets the permanent weapon that performed the hit, or null.
     * @return the permanent weapon that performed the hit, or null
     */
    public SwccgBuiltInCardBlueprint getHitByPermanentWeapon() {
        return _hitByPermanentWeapon;
    }

    /**
     * Gets the card that fired the weapon that performed the hit, or null
     * @return the card that fired the weapon that performed the hit, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Hit' " + GameUtils.getCardLink(_cardHit);
    }
}
