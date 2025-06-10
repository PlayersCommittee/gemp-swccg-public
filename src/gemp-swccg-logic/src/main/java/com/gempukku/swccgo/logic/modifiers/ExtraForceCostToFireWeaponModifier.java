package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to require extra Force cost to fire weapons.
 */
public class ExtraForceCostToFireWeaponModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _weaponFilter;

    /**
     * Creates a modifier that requires extra Force cost to fire weapons accepted by the filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param modifierAmount the amount of the modifier
     */
    public ExtraForceCostToFireWeaponModifier(PhysicalCard source, Filterable weaponFilter, int modifierAmount) {
        this(source, weaponFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that requires extra Force cost to fire weapons accepted by the filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ExtraForceCostToFireWeaponModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, int modifierAmount) {
        this(source, weaponFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that requires extra Force cost to play Interrupt cards accepted by the filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private ExtraForceCostToFireWeaponModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Evaluator evaluator) {
        super(source, null, null, condition, ModifierType.EXTRA_FORCE_COST_TO_FIRE_WEAPON, false);
        _evaluator = evaluator;
        _weaponFilter = Filters.and(weaponFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, physicalCard);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint permanentWeapon) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
