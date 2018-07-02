package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.common.SpecialRule;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the Hoth Energy Shield Rules are in effect.
 */
public class HothEnergyShieldRulesInEffectCondition implements Condition {

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard location = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Main_Power_Generators);
        if (location != null && location.getBlueprint().isSpecialRuleInEffectHere(SpecialRule.HOTH_ENERGY_SHIELD_RULES, location)) {
            if (!modifiersQuerying.hasFlagActive(gameState, ModifierFlag.HOTH_ENERGY_SHIELD_RULES_SUSPENDED)) {
                return true;
            }
        }
        return false;
    }
}
