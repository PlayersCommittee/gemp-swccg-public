package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to take off for free.
 */
public class TakesOffForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to take off for free.
     * @param source the card that is the source of the modifier and takes off for free
     */
    public TakesOffForFreeModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes affected cards to take off for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off for free
     */
    public TakesOffForFreeModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes the source card to take off for free.
     * @param source the card that is the source of the modifier and takes off for free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TakesOffForFreeModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes affected cards to take off for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off for free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TakesOffForFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.TAKES_OFF_FOR_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Takes off for free";
    }
}
