package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Lost Or Starting Interrupts.
 */
public abstract class AbstractLostOrStartingInterrupt extends AbstractInterrupt {

    /**
     * Creates a blueprint for a Lost Or Starting Interrupt.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     */
    protected AbstractLostOrStartingInterrupt(Side side, float destiny, String title) {
        this(side, destiny, title, null);
    }

    /**
     * Creates a blueprint for a Lost Or Starting Interrupt.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractLostOrStartingInterrupt(Side side, float destiny, String title, Uniqueness uniqueness) {
        super(side, destiny, title, uniqueness);
        setCardSubtype(CardSubtype.LOST_OR_STARTING);
    }
}
