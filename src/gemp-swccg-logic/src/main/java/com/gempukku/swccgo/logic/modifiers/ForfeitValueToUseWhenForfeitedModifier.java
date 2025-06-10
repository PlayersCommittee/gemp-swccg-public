package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that sets a forfeit value to use instead of a card's actual forfeit value when forfeiting.
 */
public class ForfeitValueToUseWhenForfeitedModifier extends AbstractModifier {
    private float _value;

    /**
     * Creates a modifier that sets a forfeit value to use instead of a card's actual forfeit value when forfeiting.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param value the value
     */
    public ForfeitValueToUseWhenForfeitedModifier(PhysicalCard source, Filterable affectFilter, float value) {
        this(source, affectFilter, null, value);
    }

    /**
     * Creates a modifier that sets a forfeit value to use instead of a card's actual forfeit value when forfeiting.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param value the value
     */
    private ForfeitValueToUseWhenForfeitedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float value) {
        super(source, null, affectFilter, condition, ModifierType.FORFEIT_VALUE_TO_USE, true);
        _value = value;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Forfeits for " + GuiUtils.formatAsString(_value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _value;
    }
}
