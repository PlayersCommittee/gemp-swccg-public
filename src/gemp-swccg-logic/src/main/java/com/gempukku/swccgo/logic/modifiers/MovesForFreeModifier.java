package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes specified cards to move for free.
 */
public class MovesForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to move for free.
     * @param source the card that is the source of the modifier and that moves for free
     */
    public MovesForFreeModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes the source card to move for free.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MovesForFreeModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes the cards accepted by the filter to move for free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MovesForFreeModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes the cards accepted by the filter to move for free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MovesForFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVES_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Moves for free";
    }
}
