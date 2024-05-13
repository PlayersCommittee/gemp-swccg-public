package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes 'Nighttime Conditions' to be in effect for the specified cards.
 */
public class NighttimeConditionsModifier extends KeywordModifier {

    /**
     * A modifier that causes 'Nighttime Conditions' to be in effect for the source card.
     * @param source the source of the modifier
     */
    public NighttimeConditionsModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * A modifier that causes 'Nighttime Conditions' to be in effect for cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public NighttimeConditionsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * A modifier that causes 'Nighttime Conditions' to be in effect for the source card.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public NighttimeConditionsModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * A modifier that causes 'Nighttime Conditions' to be in effect for cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public NighttimeConditionsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Keyword.NIGHTTIME_CONDITIONS);
    }
}
