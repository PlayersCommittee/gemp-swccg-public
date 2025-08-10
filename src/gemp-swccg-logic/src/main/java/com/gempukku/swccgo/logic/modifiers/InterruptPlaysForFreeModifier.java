package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes specified Interrupts to play for free.
 */
public class InterruptPlaysForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes Interrupt cards accepted by the filter to play for free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public InterruptPlaysForFreeModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes Interrupt cards accepted by the filter to play for free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public InterruptPlaysForFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.INTERRUPT_PLAYS_FOR_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Plays for free";
    }
}
