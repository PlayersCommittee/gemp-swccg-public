package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfDuelModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDuelDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to "Add X duel destiny".
 */
public class AddDuelDestinyEffect extends AddUntilEndOfDuelModifierEffect {

    /**
     * Creates an effect to "Add X duel destiny".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     */
    public AddDuelDestinyEffect(Action action, int modifierAmount) {
        this(action, modifierAmount, action.getPerformingPlayer());
    }

    /**
     * Creates an effect to "Add X duel destiny".
     * @param action the action performing this effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player with added destiny
     */
    public AddDuelDestinyEffect(Action action, int modifierAmount, String playerId) {
        super(action, new AddsDuelDestinyModifier(action.getActionSource(), modifierAmount, playerId),
                "Adds " + modifierAmount + " duel destiny");
    }
}
