package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to be targeted by specified cards.
 */
public class MayNotBeTargetedByModifier extends AbstractModifier {
    private Filter _targetedByFilter;

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     * @param targetedByFilter the targeted by filter
     */
    public MayNotBeTargetedByModifier(PhysicalCard source, Filterable targetedByFilter) {
        this(source, source, null, targetedByFilter);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param targetedByFilter the targeted by filter
     */
    public MayNotBeTargetedByModifier(PhysicalCard source, Filterable affectFilter, Filterable targetedByFilter) {
        this(source, affectFilter, null, targetedByFilter);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetedByFilter the targeted by filter
     */
    public MayNotBeTargetedByModifier(PhysicalCard source, Condition condition, Filterable targetedByFilter) {
        this(source, source, condition, targetedByFilter);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetedByFilter the targeted by filter
     */
    public MayNotBeTargetedByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetedByFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_BE_TARGETED_BY, true);
        _targetedByFilter = Filters.and(targetedByFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be targeted by specified cards";
    }

    @Override
    public boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon) {
        return (targetedBy != null && Filters.and(_targetedByFilter).accepts(gameState, modifiersQuerying, targetedBy))
                || (targetedByPermanentWeapon != null && Filters.and(_targetedByFilter).accepts(gameState, modifiersQuerying, targetedByPermanentWeapon));
    }
}
