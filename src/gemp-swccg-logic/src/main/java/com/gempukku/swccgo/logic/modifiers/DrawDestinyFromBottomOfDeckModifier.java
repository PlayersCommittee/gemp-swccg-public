package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that forces players to draw destiny from the bottom of the deck instead of the top
 */
public class DrawDestinyFromBottomOfDeckModifier extends AbstractModifier {

    /**
     * Creates a modifier that forces players to draw destiny from the bottom of the deck instead of the top
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DrawDestinyFromBottomOfDeckModifier(PhysicalCard source, Condition condition) {
        super(source, "Draw destiny from bottom of deck", Filters.any, condition, ModifierType.DRAW_DESTINY_FROM_BOTTOM_OF_DECK);
    }

}
