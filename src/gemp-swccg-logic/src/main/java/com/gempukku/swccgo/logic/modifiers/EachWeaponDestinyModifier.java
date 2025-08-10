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

import java.util.Collection;

/**
 * A modifier that affects each weapon destiny for weapons.
 */
public class EachWeaponDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _weaponFilter;
    private Filter _userFilter;
    private Filter _targetFilter;
    private boolean _noSpecifiedUser;
    private boolean _noSpecifiedTarget;

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, float modifierAmount) {
        this(source, weaponFilter, null, Filters.any, new ConstantEvaluator(modifierAmount), Filters.any);
        _noSpecifiedUser = true;
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param modifierAmount the amount of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Evaluator evaluator) {
        this(source, weaponFilter, null, Filters.any, evaluator, Filters.any);
        _noSpecifiedUser = true;
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, float modifierAmount) {
        this(source, weaponFilter, condition, Filters.any, new ConstantEvaluator(modifierAmount), Filters.any);
        _noSpecifiedUser = true;
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    protected EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Evaluator evaluator) {
        this(source, weaponFilter, condition, Filters.any, evaluator, Filters.any);
        _noSpecifiedUser = true;
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired at a target
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, float modifierAmount, Filterable targetFilter) {
        this(source, weaponFilter, null, Filters.any, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired at a target
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    protected EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        this(source, weaponFilter, condition, Filters.any, evaluator, targetFilter);
        _noSpecifiedUser = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponUserFilter the weapon user filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    protected EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Filterable weaponUserFilter, Evaluator evaluator) {
        this(source, weaponFilter, condition, weaponUserFilter, evaluator, Filters.any);
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Filterable weaponUserFilter, float modifierAmount) {
        this(source, weaponFilter, null, weaponUserFilter, new ConstantEvaluator(modifierAmount), Filters.any);
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Filterable weaponUserFilter, Evaluator evaluator) {
        this(source, weaponFilter, null, weaponUserFilter, evaluator, Filters.any);
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter at a target accepted by the target filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Filterable weaponUserFilter, float modifierAmount, Filterable targetFilter) {
        this(source, weaponFilter, null, weaponUserFilter, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponUserFilter the weapon user filter
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Filterable weaponUserFilter, float modifierAmount) {
        this(source, weaponFilter, condition, weaponUserFilter, new ConstantEvaluator(modifierAmount), Filters.any);
        _noSpecifiedTarget = true;
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter at a target accepted by the target filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponUserFilter the weapon user filter
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Filterable weaponUserFilter, float modifierAmount, Filterable targetFilter) {
        this(source, weaponFilter, condition, weaponUserFilter, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier that affects each weapon destiny for weapons accepted by the weapon filter when fired by a card
     * accepted by the weapon user filter at a target accepted by the target filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponUserFilter the weapon user filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    protected EachWeaponDestinyModifier(PhysicalCard source, Filterable weaponFilter, Condition condition, Filterable weaponUserFilter, Evaluator evaluator, Filterable targetFilter) {
        super(source, null, null, condition, ModifierType.EACH_WEAPON_DESTINY, true);
        _evaluator = evaluator;
        _weaponFilter = Filters.and(weaponFilter);
        _userFilter = Filters.and(weaponUserFilter);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public float getWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets) {
        if (((weaponCard != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, weaponCard))
                || (permanentWeapon != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon)))
                && (_noSpecifiedUser || (weaponUser!=null && Filters.and(_userFilter).accepts(gameState, modifiersQuerying, weaponUser)))
                && (_noSpecifiedTarget || Filters.canSpot(weaponTargets, gameState.getGame(), _targetFilter))) {
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, weaponCard);
        }
        return 0;
    }
}
