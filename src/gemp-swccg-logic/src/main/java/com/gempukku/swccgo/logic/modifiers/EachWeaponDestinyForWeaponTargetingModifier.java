package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;

/**
 * A modifier that affects each weapon destiny when weapon firing is targeting specified cards.
 */
public class EachWeaponDestinyForWeaponTargetingModifier extends EachWeaponDestinyModifier {

    /**
     * Creates a modifier that affects each weapon destiny when the weapon firing is targeting the source card.
     * @param source the source of the modifier and for which each each weapon destiny targeting it is modified
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyForWeaponTargetingModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, modifierAmount);
    }

    /**
     * Creates a modifier that affects each weapon destiny for a weapon firing is targeting a card accepted by the affected
     * filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards for which each each weapon destiny targeting it is modified
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyForWeaponTargetingModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        super(source, Filters.any, null, Filters.any, new ConstantEvaluator(modifierAmount), affectFilter);
    }
}
