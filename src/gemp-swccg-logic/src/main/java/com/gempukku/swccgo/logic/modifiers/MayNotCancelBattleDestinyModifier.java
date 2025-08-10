package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents battle destiny draws from being canceled.
 */
public class MayNotCancelBattleDestinyModifier extends AbstractModifier {
    private String _playerDrawing;
    private String _playerToCancel;

    /**
     * Creates a modifier that prevents either players' battle destiny draws from being canceled.
     * @param source the source of the modifier
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source) {
        this(source, Filters.any, null, null, null);
    }

    /**
     * Creates a modifier that prevents either players' battle destiny draws from being canceled.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null, null, null);
    }

    /**
     * Creates a modifier that prevents either players' battle destiny draws from being canceled.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, Condition condition) {
        this(source, Filters.any, null, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being canceled.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be canceled
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, String playerDrawing) {
        this(source, Filters.any, playerDrawing, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being canceled.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, String playerDrawing, Condition condition) {
        this(source, Filters.any, playerDrawing, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being canceled by a specified player.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be canceled
     * @param playerToCancel the player that may not cancel battle destiny draws
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, String playerDrawing, String playerToCancel) {
        this(source, Filters.any, playerDrawing, null, playerToCancel);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being canceled by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose battle destiny draws may not be canceled
     * @param playerToCancel the player that may not cancel battle destiny draws
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, String playerToCancel) {
        this(source, locationFilter, playerDrawing, null, playerToCancel);
    }

    /**
     * Creates a modifier that prevents battle destiny draws from being canceled by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToCancel the player that may not cancel battle destiny draws
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerToCancel) {
        this(source, locationFilter, null, condition, playerToCancel);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being canceled by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose battle destiny draws may not be canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToCancel the player that may not cancel battle destiny draws
     */
    public MayNotCancelBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, Condition condition, String playerToCancel) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY, true);
        _playerDrawing = playerDrawing;
        _playerToCancel = playerToCancel;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean mayNotCancelBattleDestiny(String playerDrawing, String playerToCancel) {
        return (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToCancel == null || _playerToCancel.equals(playerToCancel));
    }
}
