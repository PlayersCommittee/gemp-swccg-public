package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes the affected cards to be excluded from battles.
 */
public class ExcludedFromBattleModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to be excluded from battles.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public ExcludedFromBattleModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Excluded from battle", affectFilter, ModifierType.EXCLUDED_FROM_BATTLE);
    }
}
