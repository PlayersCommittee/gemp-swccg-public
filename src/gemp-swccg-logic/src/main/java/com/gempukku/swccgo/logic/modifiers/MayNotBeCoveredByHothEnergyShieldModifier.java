package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

public class MayNotBeCoveredByHothEnergyShieldModifier extends AbstractModifier {
    public MayNotBeCoveredByHothEnergyShieldModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be covered by Hoth Energy Shield", affectFilter, ModifierType.MAY_NOT_BE_COVERED_BY_HOTH_ENERGY_SHIELD);
    }
}
