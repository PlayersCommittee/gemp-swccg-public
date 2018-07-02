package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that modifies power of cards accepted by the specified filter until the end of the player's next turn.
 */
public class ModifyPowerUntilEndOfPlayersNextTurnEffect extends AddUntilEndOfPlayersNextTurnModifierEffect {

    /**
     * Creates an effect that modifies power of cards accepted by the specified filter until the end of the player's next turn.
     * @param action the action
     * @param playerId the player
     * @param affectFilter the filter
     * @param modifierAmount the amount of power to modify
     * @param actionMsg the message to send about the modifier
     */
    public ModifyPowerUntilEndOfPlayersNextTurnEffect(Action action, String playerId, Filterable affectFilter, int modifierAmount, String actionMsg) {
        super(action, playerId, new PowerModifier(action.getActionSource(), affectFilter, modifierAmount), actionMsg);
    }
}
