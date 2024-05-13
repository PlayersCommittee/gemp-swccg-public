package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier for "The number of battle destiny draws may not be limited for either player" at specified battle locations.
 */
public class NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier extends AbstractModifier {

    /**
     * Creates a "The number of battle destiny draws may not be limited for either player" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     */
    public NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier(PhysicalCard source, Filterable locationFilter) {
        super(source, "Number of battle destiny draws may not be limited for either player", Filters.and(locationFilter, Filters.battleLocation), ModifierType.BATTLE_DESTINY_DRAWS_MAY_NOT_BE_LIMITED_FOR_EITHER_PLAYER, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Number of battle destiny draws drawn by either player may not be limited";
    }
}
