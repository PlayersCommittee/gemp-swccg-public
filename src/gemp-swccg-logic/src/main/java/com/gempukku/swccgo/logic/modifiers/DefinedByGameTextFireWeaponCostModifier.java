package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to define the initial printed fire weapon cost of a weapon.
 * This is used when the printed fire weapon cost of a weapon is defined by game text.
 */
public class DefinedByGameTextFireWeaponCostModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _weaponUserFilter;

    /**
     * Creates a modifier to define the initial printed fire weapon cost of a weapon.
     * @param source the source of the modifier
     * @param weaponFilter the filter for weapons whose printed fire weapon cost is defined
     * @param weaponUserFilter the weapon user filter
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextFireWeaponCostModifier(PhysicalCard source, Filterable weaponFilter, Filterable weaponUserFilter, int modifierAmount) {
        this(source, weaponFilter, weaponUserFilter, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to define the initial printed fire weapon cost of a weapon.
     * @param source the source of the modifier
     * @param weaponFilter the filter for weapons whose printed fire weapon cost is defined
     * @param weaponUserFilter the weapon user filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefinedByGameTextFireWeaponCostModifier(PhysicalCard source, Filterable weaponFilter, Filterable weaponUserFilter, Evaluator evaluator) {
        super(source, null, weaponFilter, null, ModifierType.PRINTED_FIRE_WEAPON_COST, true);
        _evaluator = evaluator;
        _weaponUserFilter = Filters.and(weaponUserFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean isDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser) {
        return weaponUser != null && Filters.and(_weaponUserFilter).accepts(gameState, modifiersQuerying, weaponUser);
    }

    @Override
    public float getDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, weaponUser);
    }
}
