package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier for not being able to exist (deploy or move to) specified locations.
 */
public class MayNotExistAtLocationModifier extends MayNotExistAtTargetModifier {

    /**
     * Creates a modifier for not being able to exist (deploy or move to) specified locations.
     * @param source the card that is the source of the modifier and that may not exist at specified locations
     * @param locationFilter the filter for locations that affected cards may not exist at
     */
    public MayNotExistAtLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, locationFilter);
    }

    /**
     * Creates a modifier for not being able to exist (deploy or move to) specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not exist at specified locations
     * @param locationFilter the filter for locations that affected cards may not exist at
     */
    public MayNotExistAtLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        super(source, affectFilter, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }
}
