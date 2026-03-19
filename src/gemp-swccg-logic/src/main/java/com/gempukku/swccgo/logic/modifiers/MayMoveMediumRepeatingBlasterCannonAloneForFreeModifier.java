package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows a specified character to move a Medium Repeating Blaster Cannon alone (for free)
 */
public class MayMoveMediumRepeatingBlasterCannonAloneForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows a character accepted by the filter to move a Medium Repeating Blaster Cannon alone (for free)
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayMoveMediumRepeatingBlasterCannonAloneForFreeModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, null, Filters.and(Filters.character, affectFilter), null, ModifierType.MAY_MOVE_MEDIUM_REPEATING_BLASTER_CANNON_ALONE_FOR_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May move Medium Repeating Blaster Cannon alone (for free)";
    }
}
