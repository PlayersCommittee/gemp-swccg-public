package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotRemoveJustLostCardsFromLostPileModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotRemoveJustLostCardsFromLostPileModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotRemoveJustLostCardsFromLostPileModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be removed from lost pile", affectFilter, condition, ModifierType.MAY_NOT_REMOVE_JUST_LOST_CARDS_FROM_LOST_PILE);
    }
}
