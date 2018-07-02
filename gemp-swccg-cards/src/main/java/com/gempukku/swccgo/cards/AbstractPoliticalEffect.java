package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Political Effects.
 */
public abstract class AbstractPoliticalEffect extends AbstractEffect {

    /**
     * Creates a blueprint for a Political Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractPoliticalEffect(Side side, float destiny, String title, Uniqueness uniqueness) {
        super(side, destiny, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, uniqueness);
        setCardSubtype(CardSubtype.POLITICAL);
    }
}
