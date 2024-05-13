package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to "Add X battle destiny".
 */
public class AddBattleDestinyEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect to "Add X battle destiny".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     */
    public AddBattleDestinyEffect(Action action, int modifierAmount) {
        this(action, modifierAmount, action.getPerformingPlayer());
    }

    /**
     * Creates an effect to "Add X battle destiny".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player with added destiny
     */
    public AddBattleDestinyEffect(Action action, int modifierAmount, String playerId) {
        super(action, new AddsBattleDestinyModifier(action.getActionSource(), modifierAmount, playerId, true),
                "Adds " + modifierAmount + " battle destiny");
    }
}
