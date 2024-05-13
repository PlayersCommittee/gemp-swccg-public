package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to be battled.
 */
public class MayBeBattledModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the source card to be battled.
     * @param source the card that is the source of the modifier and that is allowed to be battled
     */
    public MayBeBattledModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be battled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayBeBattledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be battled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeBattledModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May be battled", affectFilter, condition, ModifierType.MAY_BE_BATTLED);
    }
}
