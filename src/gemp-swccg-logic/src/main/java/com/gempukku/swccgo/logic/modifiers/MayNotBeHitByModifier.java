package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not being able to be 'hit' by specified cards.
 */
public class MayNotBeHitByModifier extends AbstractModifier {
    private Filter _hitByFilter;

    /**
     * Creates a modifier for not being able to be 'hit' by cards accepted by the hit by filter.
     * @param source the card that is the source of the modifier and that may not be targeted
     * @param hitByFilter the hit by filter
     */
    public MayNotBeHitByModifier(PhysicalCard source, Filterable hitByFilter) {
        this(source, source, null, hitByFilter);
    }

    /**
     * Creates a modifier for not being able to be 'hit' by cards accepted by the hit by filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be 'hit'
     * @param hitByFilter the hit by filter
     */
    public MayNotBeHitByModifier(PhysicalCard source, Filterable affectFilter, Filterable hitByFilter) {
        this(source, affectFilter, null, hitByFilter);
    }

    /**
     * Creates a modifier for not being able to be 'hit' by cards accepted by the hit by filter.
     * @param source the card that is the source of the modifier and that may not be 'hit'
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param hitByFilter the hit by filter
     */
    public MayNotBeHitByModifier(PhysicalCard source, Condition condition, Filterable hitByFilter) {
        this(source, source, condition, hitByFilter);
    }

    /**
     * Creates a modifier for not being able to be 'hit' by cards accepted by the hit by filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be 'hit'
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param hitByFilter the hit by filter
     */
    public MayNotBeHitByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable hitByFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_BE_HIT_BY, true);
        _hitByFilter = Filters.and(hitByFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be 'hit' by specified cards";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetedBy) {
        return Filters.and(_hitByFilter).accepts(gameState, modifiersQuerying, targetedBy);
    }
}
