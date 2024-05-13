package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to "Add X destiny to attrition".
 */
public class AddDestinyToAttritionEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect to "Add X destiny to attrition".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     */
    public AddDestinyToAttritionEffect(Action action, int modifierAmount) {
        this(action, modifierAmount, action.getPerformingPlayer());
    }

    /**
     * Creates an effect to "Add X destiny to attrition".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player with added attrition for opponent
     */
    public AddDestinyToAttritionEffect(Action action, int modifierAmount, String playerId) {
        super(action, new AddsDestinyToAttritionModifier(action.getActionSource(), modifierAmount, playerId, true),
                "Adds " + modifierAmount + " destiny to attrition");
    }
}
