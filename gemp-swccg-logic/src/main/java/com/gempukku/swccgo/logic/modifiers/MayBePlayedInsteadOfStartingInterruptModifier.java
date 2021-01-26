package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows a card to be played instead of a starting interrupt
 */
public class MayBePlayedInsteadOfStartingInterruptModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows a card to be played instead of a starting interrupt
     * @param source the card that may be played instead of a starting interrupt
     */
    public MayBePlayedInsteadOfStartingInterruptModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that allows a card to be played instead of a starting interrupt
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayBePlayedInsteadOfStartingInterruptModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows a card to be played instead of a starting interrupt
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition
     */
    public MayBePlayedInsteadOfStartingInterruptModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be Disarmed", affectFilter, condition, ModifierType.MAY_BE_PLAYED_INSTEAD_OF_STARTING_INTERRUPT);
    }
}
