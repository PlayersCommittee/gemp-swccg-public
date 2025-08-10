package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePurchasedModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;

/**
 * Enforces the game rule that specifies that a card with [Presence icon] is immune to Restraining Bolt and may not be 'purchased'.
 */
public class PresenceIconRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    /**
     * Creates a rule that enforces the game rule that specifies that a card with [Presence icon] is immune to Restraining
     * Bolt and may not be 'purchased'.
     * @param modifiersEnvironment the modifiers environment
     */
    public PresenceIconRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new ImmuneToTitleModifier(null, Icon.PRESENCE, Title.Restraining_Bolt));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotBePurchasedModifier(null, Icon.PRESENCE));
    }
}
