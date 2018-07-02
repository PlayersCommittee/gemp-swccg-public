package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.conditions.HothEnergyShieldRulesInEffectCondition;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.UnderHothEnergyShieldModifier;

/**
 * Enforces the game rule that specifies which Hoth sites are under the Hoth Energy Shield.
 */
public class HothEnergyShieldRule {
    private ModifiersEnvironment _modifiersEnvironment;

    /**
     * Creates a rule that enforces the game rule that specifies which Hoth sites are under the Hoth Energy Shield.
     * @param modifiersEnvironment the modifiers environment
     */
    public HothEnergyShieldRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        Filter filter = Filters.and(Filters.or(Filters.Echo_site, Filters.First_Marker, Filters.Second_Marker, Filters.Third_Marker), Filters.in_play);
        _modifiersEnvironment.addAlwaysOnModifier(
                new UnderHothEnergyShieldModifier(null, filter, new HothEnergyShieldRulesInEffectCondition()));
    }
}
