package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Deactivate The Shield Generator total.
 */
public class RestoreFreedomToTheGalaxyTotalModifier extends EpicEventCalculationTotalModifier {

    /**
     * Creates a modifier to Deactivate The Shield Generator total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public RestoreFreedomToTheGalaxyTotalModifier(PhysicalCard source, int modifierAmount) {
        super(source, Filters.Restore_Freedom_To_The_Galaxy, null, modifierAmount, false);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Restore Freedom To The Galaxy total +" + GuiUtils.formatAsString(value);
        else
            return "Restore Freedom To The Galaxy total " + GuiUtils.formatAsString(value);
    }
}
