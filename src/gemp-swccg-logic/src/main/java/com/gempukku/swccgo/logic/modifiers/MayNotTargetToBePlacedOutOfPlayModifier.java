package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents affected cards from being targeted to be placed out of play.
 */
public class MayNotTargetToBePlacedOutOfPlayModifier extends AbstractModifier {
    private Filter _targetedByFilter;

    /**
     * Creates a modifier that prevents affected cards from being targeted to be placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param targetedByFilter the filter for card doing the targeting
     */
    public MayNotTargetToBePlacedOutOfPlayModifier(PhysicalCard source, Filterable affectFilter, Filterable targetedByFilter) {
        this(source, affectFilter, targetedByFilter, null);
    }

    /**
     * Creates a modifier that prevents affected cards from being targeted to be placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param targetedByFilter the filter for card doing the targeting
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotTargetToBePlacedOutOfPlayModifier(PhysicalCard source, Filterable affectFilter, Filterable targetedByFilter, Condition condition) {
        super(source, "May not target to be placed out of play by specified cards", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_PLACED_OUT_OF_PLAY_BY);
        _targetedByFilter = Filters.and(targetedByFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_targetedByFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}
