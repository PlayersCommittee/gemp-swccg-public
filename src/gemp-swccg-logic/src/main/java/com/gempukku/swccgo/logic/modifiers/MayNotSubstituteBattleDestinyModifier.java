package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents battle destiny draws from being substituted by the specified player.
 */
public class MayNotSubstituteBattleDestinyModifier extends AbstractModifier {

   /**
     * Creates a modifier that prevents battle destiny draws from being substituted by the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not substitute battle destiny draws
     */
    public MayNotSubstituteBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_SUBSTITUTE_BATTLE_DESTINY, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }
}
