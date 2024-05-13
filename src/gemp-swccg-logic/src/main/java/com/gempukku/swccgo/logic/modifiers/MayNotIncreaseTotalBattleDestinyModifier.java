package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents total battle destiny from being increased.
 */
public class MayNotIncreaseTotalBattleDestinyModifier extends AbstractModifier {
    private String _playerDrawing;
    private String _playerToIncrease;

    /**
     * Creates a modifier that prevents either players' total battle destiny from being increased.
     * @param source the source of the modifier
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source) {
        this(source, Filters.any, null, null, null);
    }

    /**
     * Creates a modifier that prevents either players' total battle destiny from being increased.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, Condition condition) {
        this(source, Filters.any, null, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being increased.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be increased
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing) {
        this(source, Filters.any, playerDrawing, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being increased.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be increased
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing, Condition condition) {
        this(source, Filters.any, playerDrawing, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being increased by a specified player.
     * @param source the source of the modifier
     * @param playerDrawing the player whose total battle destiny may not be increased
     * @param playerToIncrease the player that may not increase total battle destiny
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, String playerDrawing, String playerToIncrease) {
        this(source, Filters.any, playerDrawing, null, playerToIncrease);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being increased by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose total battle destiny may not be increased
     * @param playerToIncrease the player that may not increase total battle destiny
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, String playerToIncrease) {
        this(source, locationFilter, playerDrawing, null, playerToIncrease);
    }

    /**
     * Creates a modifier that prevents total battle destiny from being increased by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToIncrease the player that may not increase total battle destiny
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerToIncrease) {
        this(source, locationFilter, null, condition, playerToIncrease);
    }

    /**
     * Creates a modifier that prevents the specified player's total battle destiny from being increased by a specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerDrawing the player whose total battle destiny may not be increased
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToIncrease the player that may not increase total battle destiny
     */
    public MayNotIncreaseTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerDrawing, Condition condition, String playerToIncrease) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_INCREASE_TOTAL_BATTLE_DESTINY, true);
        _playerDrawing = playerDrawing;
        _playerToIncrease = playerToIncrease;
    }

    public boolean mayNotIncreaseBattleDestiny(String playerDrawing, String playerToIncrease) {
        return (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToIncrease == null || _playerToIncrease.equals(playerToIncrease));
    }
}
