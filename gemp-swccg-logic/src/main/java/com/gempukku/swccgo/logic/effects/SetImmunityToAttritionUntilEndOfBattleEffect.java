package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that sets immunity to attrition of the card calling the Effect to the specified number until the end of the battle.
 */

 public class SetImmunityToAttritionUntilEndOfBattleEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect that sets immunity to attrition of the card calling the Effect to the specified number until the end of the battle.
     * @param action the action
     * @param modifierAmount the amount of attrition immune to less than
     * @param actionMsg the message to send about the modifier
     */
    public SetImmunityToAttritionUntilEndOfBattleEffect(Action action, float modifierAmount, String actionMsg) {
        super(action, new ImmuneToAttritionLessThanModifier(action.getActionSource(), modifierAmount), actionMsg);
    }

 }