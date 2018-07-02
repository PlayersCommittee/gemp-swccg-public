package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents cards from joining a search party.
 */
public class MayNotJoinSearchPartyModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from joining a search party.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotJoinSearchPartyModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not join search party", affectFilter, ModifierType.MAY_NOT_JOIN_SEARCH_PARTY);
    }
}
