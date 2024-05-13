package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affect cards to have scomp links disabled.
 */
public class DisableScompLinkModifier extends CancelIconModifier {

    /**
     * Creates a modifier that causes the source card to have its scomp links disabled.
     * @param source the card that is the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DisableScompLinkModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to have scomp links disabled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public DisableScompLinkModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to have scomp links disabled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DisableScompLinkModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Icon.SCOMP_LINK);
    }
}
