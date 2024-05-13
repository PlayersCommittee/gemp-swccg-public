package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents Force drain bonuses from specified cards from being canceled.
 */
public class ForceDrainBonusesMayNotBeCanceledModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that prevents Force drain bonuses from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the cards that may not have their force drain bonuses canceled
     */
    public ForceDrainBonusesMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that prevents Force drain bonuses from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the cards that may not have their force drain bonuses canceled
     * @param locationFilter the locations where the force drain must happen to protect the force drain bonuses
     */
    public ForceDrainBonusesMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, null, affectFilter, locationFilter);
    }

    /**
     * Creates a modifier that prevents Force drain bonuses from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param condition the condition
     * @param affectFilter the filter
     */
    public ForceDrainBonusesMayNotBeCanceledModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        this(source, condition, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that prevents Force drain bonuses from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param condition the condition
     * @param affectFilter the filter
     */
    public ForceDrainBonusesMayNotBeCanceledModifier(PhysicalCard source, Condition condition, Filterable affectFilter, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.FORCE_DRAIN_BONUSES_MAY_NOT_BE_CANCELED, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    public boolean isForceDrainAtLocationFilter(SwccgGame game, PhysicalCard location) {
        return _locationFilter.accepts(game, location);
    }
}
