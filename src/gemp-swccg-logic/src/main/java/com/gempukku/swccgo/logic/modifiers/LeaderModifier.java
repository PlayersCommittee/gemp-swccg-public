package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affect cards to become leaders.
 */
public class LeaderModifier extends KeywordModifier {

    /**
     * Creates a modifier that causes the source card to become a leader.
     * @param source the card that is the source of the modifier and that becomes a leader
     */
    public LeaderModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes the source card to become a leader.
     * @param source the card that is the source of the modifier and that becomes a leader
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public LeaderModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to become leaders.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public LeaderModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to become leaders.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public LeaderModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Keyword.LEADER);
    }
}
