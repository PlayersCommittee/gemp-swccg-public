package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to be placed out of play when completed if played as the specified subtype.
 */
public class InterruptPlacedOutOfPlayWhenCompletedModifier extends AbstractModifier {
    private CardSubtype _subtype;
    /**
     * Creates a modifier that causes Interrupts accepted by the filter to be placed out of play when completed if played as the specified subtype.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param subtype the subtype required for the card to be placed out play
     */
    public InterruptPlacedOutOfPlayWhenCompletedModifier(PhysicalCard source, Filterable affectFilter, CardSubtype subtype) {
        this(source, affectFilter, null, subtype);
    }

    /**
     * Creates a modifier that causes Interrupts accepted by the filter to be placed out of play when completed.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param subtype the subtype required for the card to be placed out of play
     */
    public InterruptPlacedOutOfPlayWhenCompletedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, CardSubtype subtype) {
        super(source, "Place out of play when completed if played as a "+subtype.getHumanReadable(), Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.PLACED_OUT_OF_PLAY_WHEN_COMPLETED);
        _subtype = subtype;
    }

    public boolean affectsSubtype(CardSubtype subtype) {
        return subtype == _subtype;
    }
}