package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that modifies total power in battle for the specified player until the end of battle.
 */
public class ModifyTotalPowerUntilEndOfBattleEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect that modifies total power in battle for the specified player until the end of battle.
     * @param action the action
     * @param modifierAmount the amount of total power to modify
     * @param playerId the player whose total power is modified
     * @param actionMsg the message to send about the modifier
     */
    public ModifyTotalPowerUntilEndOfBattleEffect(Action action, float modifierAmount, String playerId, String actionMsg) {
        super(action, new TotalPowerModifier(action.getActionSource(), Filters.battleLocation, modifierAmount, playerId), actionMsg);
    }
}
