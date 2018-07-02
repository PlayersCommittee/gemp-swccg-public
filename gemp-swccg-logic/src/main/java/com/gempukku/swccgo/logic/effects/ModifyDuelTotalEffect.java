package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that modifies the duel total for the specified player until the end of the duel.
 */
public class ModifyDuelTotalEffect extends AddUntilEndOfDuelModifierEffect {

    /**
     * Creates an effect that modifies duel total for the specified player until the end of the duel.
     * @param action the action
     * @param modifierAmount the amount to modify
     * @param playerId the player
     * @param actionMsg the message to send about the modifier
     */
    public ModifyDuelTotalEffect(Action action, float modifierAmount, String playerId, String actionMsg) {
        super(action, new DuelTotalModifier(action.getActionSource(), Filters.and(Filters.owner(playerId), Filters.participatingInDuel), modifierAmount), actionMsg);
    }
}
