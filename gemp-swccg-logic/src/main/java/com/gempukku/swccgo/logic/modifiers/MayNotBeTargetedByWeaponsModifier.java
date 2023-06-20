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
public class MayNotBeTargetedByWeaponsModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     */
    public MayNotBeTargetedByWeaponsModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     */
    public MayNotBeTargetedByWeaponsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeTargetedByWeaponsModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeTargetedByWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        this(source, affectFilter, condition, Filters.weapon);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponFilter the filter for weapons that may not target
     */
    protected MayNotBeTargetedByWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable weaponFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_BE_TARGETED_BY, true);
        _weaponFilter = Filters.and(Filters.weapon, weaponFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be targeted by weapons";
    }

    @Override
    public boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon) {
        return (targetedByPermanentWeapon != null && _weaponFilter.accepts(gameState.getGame(), targetedByPermanentWeapon) && !targetedByPermanentWeapon.getPhysicalCard(gameState.getGame()).getOwner().equals(cardToTarget.getOwner()))
                || (targetedBy != null && _weaponFilter.accepts(gameState.getGame(), targetedBy) && !targetedBy.getOwner().equals(cardToTarget.getOwner()) && Filters.weapon.accepts(gameState, modifiersQuerying, targetedBy));
    }
}
