package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels immunity to attrition of cards accepted by the specified filter until the end of the turn.
 */
public class CancelImmunityToAttritionUntilEndOfTurnEffect extends AddUntilEndOfTurnModifierEffect {

    /**
     * Creates an effect that cancels immunity to attrition of cards accepted by the specified filter until the end of the turn.
     * @param action the action
     * @param affectFilter the filter
     * @param actionMsg the message to send about the modifier
     */
    public CancelImmunityToAttritionUntilEndOfTurnEffect(Action action, Filterable affectFilter, String actionMsg) {
        super(action, new CancelImmunityToAttritionModifier(action.getActionSource(), affectFilter), actionMsg);
    }
}
