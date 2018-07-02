package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the game text of affected cards to be canceled.
 * Note: The affected cards' game texts are actually canceled by an action triggered by the CancelGameTextRule class.
 */
public class CancelsGameTextModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the game text of cards accepted by the filter to be canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public CancelsGameTextModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes the game text of cards accepted by the filter to be canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CancelsGameTextModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.onTable, affectFilter), condition, ModifierType.CANCEL_GAME_TEXT, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return self.isGameTextCanceled() ? "Game text canceled" : null;
    }
}
