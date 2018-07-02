package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents total battle destiny from being reset.
 */
public class MayNotResetTotalBattleDestinyModifier extends AbstractModifier {
    private String _playerDrawing;
    private String _playerToReset;

    /**
     * Creates a modifier that prevents either players' total battle destiny from being reset.
     * @param source the source of the modifier
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source) {
        this(source, Filters.any, null, null, null);
    }

    /**
     * Creates a modifier that prevents either players' total battle destiny from being reset.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, Condition condition) {
        this(source, Filters.any, null, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being reset.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be reset
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing) {
        this(source, Filters.any, playerDrawing, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being reset.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing, Condition condition) {
        this(source, Filters.any, playerDrawing, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being reset by a specified player.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be reset
     * @param playerToReset the player that may not reset total battle destiny
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing, String playerToReset) {
        this(source, Filters.any, playerDrawing, null, playerToReset);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being reset by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose total battle destiny may not be reset
     * @param playerToReset the player that may not reset total battle destiny
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, String playerToReset) {
        this(source, locationFilter, playerDrawing, null, playerToReset);
    }

    /**
     * Creates a modifier that prevents total battle destiny from being reset by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToReset the player that may not reset total battle destiny
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerToReset) {
        this(source, locationFilter, null, condition, playerToReset);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being reset by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose total battle destiny may not be reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToReset the player that may not reset total battle destiny
     */
    public MayNotResetTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, Condition condition, String playerToReset) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_RESET_TOTAL_BATTLE_DESTINY, true);
        _playerDrawing = playerDrawing;
        _playerToReset = playerToReset;
    }

    @Override
    public boolean mayNotResetBattleDestiny(String playerDrawing, String playerToReset) {
        return (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToReset == null || _playerToReset.equals(playerToReset));
    }
}
