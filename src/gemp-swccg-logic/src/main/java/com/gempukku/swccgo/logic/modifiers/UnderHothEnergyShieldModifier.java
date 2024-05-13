package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

public class UnderHothEnergyShieldModifier extends AbstractModifier {
    public UnderHothEnergyShieldModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Under Hoth Energy Shield", affectFilter, ModifierType.UNDER_HOTH_ENERGY_SHIELD);
    }
}
