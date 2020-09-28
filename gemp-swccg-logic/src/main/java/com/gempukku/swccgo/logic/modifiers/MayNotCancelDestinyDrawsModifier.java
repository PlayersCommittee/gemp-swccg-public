package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents all destiny draws from being canceled.
 */
public class MayNotCancelDestinyDrawsModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents a player from cancelling any destiny draws.
     *
     * @param source   the source of the modifier
     * @param playerId the player who may not cancel destiny draws
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, String playerId) {
        super(source, null, Filters.any, null, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS, true);
        _playerId = playerId;
    }

    /**
     * Creates a modifier that prevents a player from cancelling any destiny draws.
     * @param source the source of the modifier
     * @param condition conditions which must be met for the modifier to be active
     * @param playerId the player who may not cancel destiny draws
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, null, Filters.any, condition, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }
}
