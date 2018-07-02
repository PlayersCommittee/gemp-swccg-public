package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Mobile Effects.
 */
public abstract class AbstractMobileEffect extends AbstractEffect {

    /**
     * Creates a blueprint for a Mobile Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractMobileEffect(Side side, float destiny, String title, Uniqueness uniqueness) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, title, uniqueness);
        setCardSubtype(CardSubtype.MOBILE);
    }
}
