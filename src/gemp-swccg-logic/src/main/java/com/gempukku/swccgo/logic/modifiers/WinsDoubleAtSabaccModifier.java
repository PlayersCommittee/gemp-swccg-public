package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A 'wins double' at sabacc modifier.
 */
public class WinsDoubleAtSabaccModifier extends AbstractModifier {

    /**
     * Creates a 'wins double' at sabacc modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that 'win double' when playing sabacc
     */
    public WinsDoubleAtSabaccModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a 'wins double' at sabacc modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that 'win double' when playing sabacc
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public WinsDoubleAtSabaccModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "'Wins double' at sabacc", affectFilter, condition, ModifierType.WINS_DOUBLE_AT_SABACC, false);
    }
}
