package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;

/**
 * Enforces the special Jabba's Palace Sealed game rule that any character of ability 1 who has a printed deploy number
 * of 3 or greater is considered, for all purposes, to be ability 2.
 */
public class JabbasPalaceSealedRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    /**
     * Creates a rule that enforces the special Jabba's Palace Sealed game rule that any character of ability 1 who has
     * a printed deploy number of 3 or greater is considered, for all purposes, to be ability 2.
     * @param modifiersEnvironment the modifiers environment
     */
    public JabbasPalaceSealedRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new DefinedByGameTextAbilityModifier(null, Filters.and(Filters.character, Filters.printedAbilityEqualTo(1), Filters.printedDeployCostMoreThanOrEqualTo(3)), 2));
    }
}
