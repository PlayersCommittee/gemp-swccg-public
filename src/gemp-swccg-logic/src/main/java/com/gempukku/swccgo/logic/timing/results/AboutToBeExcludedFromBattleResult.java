package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be excluded from battle by a card.
 */
public class AboutToBeExcludedFromBattleResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeExcluded;
    private PhysicalCard _excludedByCard;
    private SwccgBuiltInCardBlueprint _excludedByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be excluded from battle by a card.
     * @param action the action
     * @param cardToBeExcluded the card to be 'excluded from battle'
     * @param excludedByCard the card that is performing the excluding from battle
     * @param excludedByPermanentWeapon the permanent weapon that is performing the hit, or null
     * @param cardFiringWeapon the card that fired the weapon that is performing the excluding from battle, or null
     * @param effect the effect that can be used to prevent the card from being 'excluded from battle'
    */
    public AboutToBeExcludedFromBattleResult(Action action, PhysicalCard cardToBeExcluded, PhysicalCard excludedByCard, SwccgBuiltInCardBlueprint excludedByPermanentWeapon, PhysicalCard cardFiringWeapon, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_EXCLUDED_FROM_BATTLE, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToBeExcluded = cardToBeExcluded;
        _excludedByCard = excludedByCard;
        _excludedByPermanentWeapon = excludedByPermanentWeapon;
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
     * Gets the card to be excluded from battle.
     * @return the card
     */
    public PhysicalCard getCardToBeExcluded() {
        return _cardToBeExcluded;
    }

    /**
     * Gets the card that is performing the excluding from battle.
     * @return the card that is performing the excluding from battle
     */
    public PhysicalCard getExcludedByCard() {
        return _excludedByCard;
    }

    /**
     * Gets the permanent weapon that is performing the excluding from battle, or null.
     * @return the permanent weapon that is performing the excluding from battle, or null
     */
    public SwccgBuiltInCardBlueprint getExcludedByPermanentWeapon() {
        return _excludedByPermanentWeapon;
    }

    /**
     * Gets the card that fired the weapon that is performing the excluding from battle, or null
     * @return the card that fired the weapon that is performing the excluding from battle, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Gets the interface that can be used to prevent the card from being 'excluded from battle'.
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
        return "About to exclude " + GameUtils.getCardLink(_cardToBeExcluded) + " from battle";
    }
}
