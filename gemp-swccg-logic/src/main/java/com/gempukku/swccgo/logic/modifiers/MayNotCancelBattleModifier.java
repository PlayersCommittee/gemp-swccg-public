package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents battles from being canceled.
 */
public class MayNotCancelBattleModifier extends AbstractModifier {
    private String _playerToCancel;

    /**
     * Creates a modifier that prevents battles from being canceled by either player.
     * @param source the source of the modifier
     */
    public MayNotCancelBattleModifier(PhysicalCard source) {
        this(source, Filters.any, null, null);
    }

    /**
     * Creates a modifier that prevents battles from being canceled by either player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotCancelBattleModifier(PhysicalCard source, Condition condition) {
        this(source, Filters.any, condition, null);
    }

    /**
     * Creates a modifier that prevents either the specified player from canceling battles
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToCancel the player that may not cancel battles
     */
    public MayNotCancelBattleModifier(PhysicalCard source, Condition condition, String playerToCancel) {
        this(source, Filters.any, condition, playerToCancel);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling battles
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToCancel the player that may not cancel battles
     */
    public MayNotCancelBattleModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerToCancel) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_CANCEL_BATTLE, true);
        _playerToCancel = playerToCancel;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    public boolean mayNotCancelBattle(String playerToCancel) {
        return (_playerToCancel == null || _playerToCancel.equals(playerToCancel));
    }
}
