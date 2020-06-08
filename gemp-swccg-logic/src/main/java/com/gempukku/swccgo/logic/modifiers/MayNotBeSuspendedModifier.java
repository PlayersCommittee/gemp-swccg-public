package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to be suspended.
 */
public class MayNotBeSuspendedModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to be suspended.
     * @param source the card that is the source of the modifier and that may not be suspended
     */
    public MayNotBeSuspendedModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier for not being able to be suspended.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be suspended
     */
    public MayNotBeSuspendedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier not being able to be suspended.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be suspended
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeSuspendedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be suspended", affectFilter, condition, ModifierType.MAY_NOT_HAVE_GAME_TEXT_CANCELED, true);
    }
}
