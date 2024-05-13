package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to "Add X destiny to total power".
 */
public class AddDestinyToTotalPowerEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect to "Add X destiny to total power".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     */
    public AddDestinyToTotalPowerEffect(Action action, int modifierAmount) {
        this(action, modifierAmount, action.getPerformingPlayer());
    }

    /**
     * Creates an effect to "Add X destiny to total power".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player with added destiny
     */
    public AddDestinyToTotalPowerEffect(Action action, int modifierAmount, String playerId) {
        super(action, new AddsDestinyToPowerModifier(action.getActionSource(), modifierAmount, playerId, true),
                "Adds " + modifierAmount + " destiny to total power");
    }
}
