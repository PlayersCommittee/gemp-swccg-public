package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;

/**
 * A modifier that affects each weapon destiny when weapon is fired by specified cards.
 */
public class EachWeaponDestinyForWeaponFiredByModifier extends EachWeaponDestinyModifier {

    /**
     * Creates a modifier that affects each weapon destiny when the weapon is fired by the source card.
     * @param source the source of the modifier and for which each weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, int modifierAmount) {
        this(source, modifierAmount, Filters.any, Filters.any);
    }

    /**
     * Creates a modifier that affects each weapon destiny when the weapon is fired by the source card.
     * @param source the source of the modifier and for which each weapon destiny for a weapon it fires is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public EachWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, condition, modifierAmount, Filters.any, Filters.any);
    }

    /**
     * Creates a modifier that affects each weapon destiny when the weapon accepted by the weapon filter is fired by the
     * source card.
     * @param source the source of the modifier and for which each weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     * @param weaponFilter the weapon filter
     */
    public EachWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, int modifierAmount, Filter weaponFilter) {
        this(source, modifierAmount, weaponFilter, Filters.any);
    }

    /**
     * Creates a modifier that affects each weapon destiny when the weapon accepted by the weapon filter is fired by the
     * source card and is targeting a card accepted by the target filter.
     * @param source the source of the modifier and for which each weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     */
    public EachWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, int modifierAmount, Filter weaponFilter, Filter targetFilter) {
        super(source, weaponFilter, null, source, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier that affects each weapon destiny when the weapon accepted by the weapon filter is fired by the
     * source card and is targeting a card accepted by the target filter.
     * @param source the source of the modifier and for which each weapon destiny for a weapon it fires is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     */
    public EachWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, Condition condition, int modifierAmount, Filter weaponFilter, Filter targetFilter) {
        super(source, weaponFilter, condition, source, new ConstantEvaluator(modifierAmount), targetFilter);
    }
}
