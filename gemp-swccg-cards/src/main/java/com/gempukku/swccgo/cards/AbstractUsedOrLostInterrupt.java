package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Used Or Lost Interrupts.
 */
public abstract class AbstractUsedOrLostInterrupt extends AbstractInterrupt {

    /**
     * Creates a blueprint for a Used Or Lost Interrupt.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     */
    protected AbstractUsedOrLostInterrupt(Side side, float destiny, String title) {
        this(side, destiny, title, null);
    }

    /**
     * Creates a blueprint for a Used Or Lost Interrupt.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractUsedOrLostInterrupt(Side side, float destiny, String title, Uniqueness uniqueness) {
        super(side, destiny, title, uniqueness);
        setCardSubtype(CardSubtype.USED_OR_LOST);
    }
}
