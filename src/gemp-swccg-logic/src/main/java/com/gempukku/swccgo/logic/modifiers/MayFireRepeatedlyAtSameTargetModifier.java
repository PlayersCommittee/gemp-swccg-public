package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows a specified weapon to fire repeatedly at the same target for a specified cost.
 * Distinct from {@link MayFireRepeatedlyModifier} so cards that cancel/target weapon-sourced repeated
 * firing (e.g. Mandalorian Mishap) can treat character-granted same-target repeats separately.
 */
public class MayFireRepeatedlyAtSameTargetModifier extends AbstractModifier {
    private int _fireRepeatedlyCost;

    /**
     * Creates a modifier that allows the source card to fire repeatedly at the same target for a specified cost.
     * @param source the source of the modifier
     * @param fireRepeatedlyCost the cost to fire repeatedly
     */
    public MayFireRepeatedlyAtSameTargetModifier(PhysicalCard source, int fireRepeatedlyCost) {
        this(source, source, null, fireRepeatedlyCost);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to fire repeatedly at the same target for a specified cost.
     * @param source the source of the modifier
     * @param affectFilter the filter for weapons affected
     * @param fireRepeatedlyCost the cost to fire repeatedly
     */
    public MayFireRepeatedlyAtSameTargetModifier(PhysicalCard source, Filterable affectFilter, int fireRepeatedlyCost) {
        this(source, affectFilter, null, fireRepeatedlyCost);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to fire repeatedly at the same target for a specified cost.
     * @param source the source of the modifier
     * @param affectFilter the filter for weapons affected
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fireRepeatedlyCost the cost to fire repeatedly
     */
    public MayFireRepeatedlyAtSameTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int fireRepeatedlyCost) {
        super(source, null, affectFilter, condition, ModifierType.MAY_FIRE_REPEATEDLY_AT_SAME_TARGET_FOR_COST, true);
        _fireRepeatedlyCost = fireRepeatedlyCost;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May fire repeatedly at same target for " + _fireRepeatedlyCost + " Force";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _fireRepeatedlyCost;
    }
}
