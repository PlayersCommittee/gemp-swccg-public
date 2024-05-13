package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.LandspeedMayNotBeIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;

/**
 * Enforces the game rule that specifies that AT-AT's may not have their landspeed increased.
 */
public class AT_AT_LandspeedRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    /**
     * Creates a rule that enforces the game rule that specifies that AT-AT's may not have their landspeed increased.
     * @param modifiersEnvironment the modifiers environment
     */
    public AT_AT_LandspeedRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new LandspeedMayNotBeIncreasedModifier(null, Filters.AT_AT));
    }
}
