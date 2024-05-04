package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to go to Used Pile (instead of Lost Pile) when forfeited.
 */
public class ForfeitedToUsedPileModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepeted by the filter to go to Used Pile (instead of Lost Pile) when forfeited.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public ForfeitedToUsedPileModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepeted by the filter to go to Used Pile (instead of Lost Pile) when forfeited.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ForfeitedToUsedPileModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Goes to Used Pile when forfeited", affectFilter, condition, ModifierType.FORFEITED_TO_USED_PILE);
    }
}
