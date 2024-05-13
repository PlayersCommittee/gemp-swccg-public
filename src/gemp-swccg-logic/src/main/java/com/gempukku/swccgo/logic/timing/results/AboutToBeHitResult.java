package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be 'hit'.
 */
public class AboutToBeHitResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeHit;
    private PhysicalCard _hitByCard;
    private SwccgBuiltInCardBlueprint _hitByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be 'hit'.
     * @param action the action
     * @param cardToBeHit the card to be 'hit'
     * @param hitByCard the card that is performing the hit
     * @param hitByPermanentWeapon the permanent weapon that is performing the hit, or null
     * @param cardFiringWeapon the card that fired the weapon that is performing the hit, or null
     * @param effect the effect that can be used to prevent the card from being 'hit'
    */
    public AboutToBeHitResult(Action action, PhysicalCard cardToBeHit, PhysicalCard hitByCard, SwccgBuiltInCardBlueprint hitByPermanentWeapon, PhysicalCard cardFiringWeapon, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_HIT, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToBeHit = cardToBeHit;
        _hitByCard = hitByCard;
        _hitByPermanentWeapon = hitByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _effect = effect;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the card to be 'hit'.
     * @return the card
     */
    public PhysicalCard getCardToBeHit() {
        return _cardToBeHit;
    }

    /**
     * Gets the card that is performing the hit.
     * @return the card that is performing the hit
     */
    public PhysicalCard getHitByCard() {
        return _hitByCard;
    }

    /**
     * Gets the permanent weapon that is performing the hit, or null.
     * @return the permanent weapon that is performing the hit, or null
     */
    public SwccgBuiltInCardBlueprint getHitByPermanentWeapon() {
        return _hitByPermanentWeapon;
    }

    /**
     * Gets the card that fired the weapon that is performing the hit, or null
     * @return the card that fired the weapon that is performing the hit, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Gets the interface that can be used to prevent the card from being 'hit'.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to 'hit' " + GameUtils.getCardLink(_cardToBeHit);
    }
}
