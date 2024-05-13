package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows a specified weapon to fire repeatedly for a specified cost.
 */
public class MayFireRepeatedlyModifier extends AbstractModifier {
    private int _fireRepeatedlyCost;

    /**
     * Creates a modifier that allows the source card to fire repeatedly for a specified cost.
     * @param source the source of the modifier
     * @param fireRepeatedlyCost the cost to fire repeatedly
     */
    public MayFireRepeatedlyModifier(PhysicalCard source, int fireRepeatedlyCost) {
        this(source, source, null, fireRepeatedlyCost);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to fire repeatedly for a specified cost.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fireRepeatedlyCost the cost to fire repeatedly
     */
    private MayFireRepeatedlyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int fireRepeatedlyCost) {
        super(source, null, affectFilter, condition, ModifierType.MAY_FIRE_REPEATEDLY_FOR_COST, true);
        _fireRepeatedlyCost = fireRepeatedlyCost;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May fire repeatedly for " + _fireRepeatedlyCost + " Force";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _fireRepeatedlyCost;
    }
}
