package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes specified cards to shipdock for free.
 */
public class ShipdocksForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to shipdock for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that shipdock for free
     */
    public ShipdocksForFreeModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), null, ModifierType.SHIPDOCKS_FOR_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Ship-docks for free";
    }
}
