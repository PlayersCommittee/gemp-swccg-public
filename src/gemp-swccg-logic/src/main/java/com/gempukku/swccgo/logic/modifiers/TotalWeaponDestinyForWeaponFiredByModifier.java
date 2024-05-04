package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that affects total weapon destiny when weapon if fired by specified cards.
 */
public class TotalWeaponDestinyForWeaponFiredByModifier extends TotalWeaponDestinyModifier {

    /**
     * Creates a modifier that affects total weapon destiny when the weapon fired by the source card.
     * @param source the source of the modifier and for which total weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     */
    public TotalWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, modifierAmount);
    }

    /**
     * Creates a modifier that affects total weapon destiny when a weapon accepted by the weapon filter is fired by the
     * source card.
     * @param source the source of the modifier and for which total weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     * @param weaponFilter the weapon filter
     */
    public TotalWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, int modifierAmount, Filterable weaponFilter) {
        super(source, weaponFilter, null, source, new ConstantEvaluator(modifierAmount), Filters.any);
    }

    /**
     * Creates a modifier that affects total weapon destiny when a weapon accepted by the weapon filter is fired by the
     * source card.
     * @param source the source of the modifier and for which total weapon destiny for a weapon it fires is modified
     * @param evaluator the evaluator
     * @param weaponFilter the weapon filter
     */
    public TotalWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, Evaluator evaluator, Filterable weaponFilter) {
        super(source, weaponFilter, null, source, evaluator, Filters.any);
    }

    /**
     * Creates a modifier that affects each weapon destiny for a weapon fired by a card accepted by the affected
     * filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards for which total weapon destiny for a weapon it fires is modified
     * @param modifierAmount the amount of the modifier
     */
    public TotalWeaponDestinyForWeaponFiredByModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        super(source, Filters.any, null, affectFilter, new ConstantEvaluator(modifierAmount), Filters.any);
    }
}
