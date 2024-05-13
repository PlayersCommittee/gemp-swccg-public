package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being battled.
 */
public class MayNotBeBattledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the source card to be battled.
     * @param source the card that is the source of the modifier and that is prevented from being battled
     */
    public MayNotBeBattledModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being battled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeBattledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being battled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeBattledModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be battled", affectFilter, condition, ModifierType.MAY_NOT_BE_BATTLED);
    }
}
