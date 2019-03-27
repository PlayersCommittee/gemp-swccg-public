package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents all destiny draws from being canceled.
 */
public class MayNotCancelDestinyDrawsModifier extends AbstractModifier {
    /**
     * Creates a modifier that prevents either players' battle destiny draws from being canceled.
     * @param source the source of the modifier
     * @param condition conditions which must be met for the modifier to be active
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, Condition condition) {
        super(source, null, Filters.any, condition, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean mayNotCancelBattleDestiny(String playerDrawing, String playerToCancel) {
        return true;
    }
}
