package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;

public class FrozenCaptiveRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    public FrozenCaptiveRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new ResetPowerModifier(null, Filters.frozenCaptive, 0));
        _modifiersEnvironment.addAlwaysOnModifier(
                new ResetAbilityModifier(null, Filters.frozenCaptive, 0));
        _modifiersEnvironment.addAlwaysOnModifier(
                new ResetLandspeedModifier(null, Filters.frozenCaptive, 0));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotBeAttackedByModifier(null, Filters.frozenCaptive, Filters.creature));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotTargetToBeTorturedModifier(null, Filters.frozenCaptive));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotTargetToBeHitModifier(null, Filters.frozenCaptive));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotTargetToBeCapturedModifier(null, Filters.frozenCaptive));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotTargetToBeFrozenModifier(null, Filters.frozenCaptive));
        _modifiersEnvironment.addAlwaysOnModifier(
                new MayNotBeTargetedByModifier(null, Filters.frozenCaptive, Filters.seeker));
    }
}
