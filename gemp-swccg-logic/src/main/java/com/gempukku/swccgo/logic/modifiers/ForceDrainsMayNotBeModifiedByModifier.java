package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from modifying the specified player's Force drains.
 */
public class ForceDrainsMayNotBeModifiedByModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from modifying the specified player's Force drains.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerDraining the player Force draining
     */
    public ForceDrainsMayNotBeModifiedByModifier(PhysicalCard source, Filterable affectFilter, String playerDraining) {
        this(source, affectFilter, null, playerDraining);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from modifying the specified player's Force drains.
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerDraining the player Force draining
     */
    public ForceDrainsMayNotBeModifiedByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerDraining) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_MODIFY_FORCE_DRAINS_BY_USING_CARD, true);
        _playerId = playerDraining;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return true;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        String sideDrainingText = (gameState.getSide(_playerId)== Side.DARK) ? " dark side's " : "light side's ";
        return "May not modify " + sideDrainingText + "Force drain";
    }
}
