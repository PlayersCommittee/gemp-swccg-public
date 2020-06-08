package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that modifies politics of cards accepted by the specified filter until the end of the turn.
 */
public class ModifyPoliticsUntilEndOfTurnEffect extends AddUntilEndOfTurnModifierEffect {

    /**
     * Creates an effect that modifies politics of cards accepted by the specified filter until the end of the turn.
     * @param action the action
     * @param affectFilter the filter
     * @param modifierAmount the amount of politics to modify
     * @param actionMsg the message to send about the modifier
     */
    public ModifyPoliticsUntilEndOfTurnEffect(Action action, Filterable affectFilter, float modifierAmount, String actionMsg) {
        super(action, new PoliticsModifier(action.getActionSource(), affectFilter, modifierAmount), actionMsg);
    }
}
