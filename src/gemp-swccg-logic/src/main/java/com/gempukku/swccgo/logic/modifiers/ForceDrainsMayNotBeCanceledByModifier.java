package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from canceling Force drains at specified locations.
 */
public class ForceDrainsMayNotBeCanceledByModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that prevents cards accepted by the filter from canceling Force drains at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public ForceDrainsMayNotBeCanceledByModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, null, locationFilter);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from canceling the specified player's Force drains
     * at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerDraining the player Force draining
     * @param locationFilter the location filter
     */
    public ForceDrainsMayNotBeCanceledByModifier(PhysicalCard source, Filterable affectFilter, String playerDraining, Filterable locationFilter) {
        this(source, affectFilter, null, playerDraining, locationFilter);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from canceling the specified player's Force drains
     * at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerDraining the player Force draining
     * @param locationFilter the location filter
     */
    public ForceDrainsMayNotBeCanceledByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerDraining, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_CANCEL_FORCE_DRAINS_BY_USING_CARD, true);
        _playerId = playerDraining;
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId != null) {
            String sideDrainingText = (gameState.getSide(_playerId) == Side.DARK) ? " dark side's " : "light side's ";
            return "May not cancel " + sideDrainingText + "Force drains";
        }
        return "May not cancel Force drains";
    }
}
