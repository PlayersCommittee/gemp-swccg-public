package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class UnderHothEnergyShieldModifier extends AbstractModifier {
    public UnderHothEnergyShieldModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Under Hoth Energy Shield", affectFilter, condition, ModifierType.UNDER_HOTH_ENERGY_SHIELD);
    }
}
