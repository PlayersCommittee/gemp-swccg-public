package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to fire for double.
 */
public class FiresForDoubleModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to fire for free.
     *
     * @param source the card that is the source of the modifier and fires for double
     */
    public FiresForDoubleModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes the source card to fire for double.
     *
     * @param source    the card that is the source of the modifier and fires for double
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public FiresForDoubleModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes affected cards to fire for double.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards that fires for double
     */
    public FiresForDoubleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affected cards to fire for free.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards that fire for free
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     */
    public FiresForDoubleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, affectFilter, condition, ModifierType.FIRES_FOR_DOUBLE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Fires for double";
    }
}
