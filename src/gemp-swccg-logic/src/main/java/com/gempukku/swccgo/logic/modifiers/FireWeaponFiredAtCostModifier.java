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
import com.gempukku.swccgo.logic.timing.GuiUtils;

public class FireWeaponFiredAtCostModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _weaponFilter;
    private Filter _targetFilter;

    public FireWeaponFiredAtCostModifier(PhysicalCard source, int modifierAmount, Filterable weaponFilter, Filterable targetFilter) {
        this(source, source, null, modifierAmount, weaponFilter, targetFilter);
    }

    public FireWeaponFiredAtCostModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable weaponFilter, Filterable targetFilter) {
        this(source, affectFilter, null, modifierAmount, weaponFilter, targetFilter);
    }

    public FireWeaponFiredAtCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable weaponFilter, Filterable targetFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), weaponFilter, targetFilter);
    }

    public FireWeaponFiredAtCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable weaponFilter, Filterable targetFilter) {
        super(source, null, affectFilter, condition, ModifierType.FIRE_WEAPON_FIRED_AT_COST, false);
        _evaluator = evaluator;
        _weaponFilter = Filters.and(weaponFilter);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Firing cost +" + GuiUtils.formatAsString(value)+" at certain targets";
        else
            return "Firing cost " + GuiUtils.formatAsString(value)+" at certain targets";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, physicalCard);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint permanentWeapon) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon);
    }

    public boolean isAffectedFiredAtTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
