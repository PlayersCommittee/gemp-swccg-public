package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents Force drain modifiers from specified cards from being canceled.
 */
public class ForceDrainModifiersMayNotBeCanceledModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the cards that may not have their force drain modifiers canceled
     */
    public ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the cards that may not have their force drain modifiers canceled
     * @param locationFilter the locations where the force drain must happen to protect the force drain modifiers
     */
    public ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, null, affectFilter, locationFilter);
    }

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param condition the condition
     * @param affectFilter the filter
     */
    public ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        this(source, condition, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param condition the condition
     * @param affectFilter the filter
     */
    public ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Condition condition, Filterable affectFilter, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.FORCE_DRAIN_MODIFIERS_MAY_NOT_BE_CANCELED, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    public boolean isForceDrainAtLocationFilter(SwccgGame game, PhysicalCard location) {
        return _locationFilter.accepts(game, location);
    }
}
