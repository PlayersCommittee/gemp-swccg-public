package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents affected cards from being excluded from battle.
 */
public class MayNotBeExcludedFromBattle extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being excluded from battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeExcludedFromBattle(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be excluded from battle", affectFilter, ModifierType.MAY_NOT_BE_EXCLUDED_FROM_BATTLE);
    }
}
