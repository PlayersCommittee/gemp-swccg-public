package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents battle destiny draws from being modified.
 */
public class MayNotModifyBattleDestinyModifier extends AbstractModifier {
    private String _playerDrawing;
    private String _playerToModify;

    /**
     * Creates a modifier that prevents either players' battle destiny draws from being modified.
     * @param source the source of the modifier
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source) {
        this(source, Filters.any, null, null, null);
    }

    /**
     * Creates a modifier that prevents either players' battle destiny draws from being modified.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, Condition condition) {
        this(source, Filters.any, null, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being modified.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be modified
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, String playerDrawing) {
        this(source, Filters.any, playerDrawing, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being modified.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, String playerDrawing, Condition condition) {
        this(source, Filters.any, playerDrawing, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param playerDrawing the player whose battle destiny draws may not be modified
     * @param playerToModify the player that may not modify battle destiny draws
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, String playerDrawing, String playerToModify) {
        this(source, Filters.any, playerDrawing, null, playerToModify);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose battle destiny draws may not be modified
     * @param playerToModify the player that may not modify battle destiny draws
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, String playerToModify) {
        this(source, locationFilter, playerDrawing, null, playerToModify);
    }

    /**
     * Creates a modifier that prevents battle destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToModify the player that may not modify battle destiny draws
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerToModify) {
        this(source, locationFilter, null, condition, playerToModify);
    }

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose battle destiny draws may not be modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToModify the player that may not modify battle destiny draws
     */
    public MayNotModifyBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, Condition condition, String playerToModify) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_MODIFY_BATTLE_DESTINY, true);
        _playerDrawing = playerDrawing;
        _playerToModify = playerToModify;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean mayNotModifyBattleDestiny(String playerDrawing, String playerToModify) {
        return (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToModify == null || _playerToModify.equals(playerToModify));
    }
}
