package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 2
 */
public class Card1_299 extends AbstractStarfighter {
    public Card1_299() {
        super(Side.DARK, 2, 1, 1, null, 4, null, 3, "Black 2", Uniqueness.UNIQUE);
        setLore("TIE/ln assigned to pilot DS-61-2. Has 27 'flames' on cockpit, one for each Rebel kill. Control yoke has a holo of Mithels' young son, Rejili.");
        setGameText("May add 1 pilot. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.BLACK_2);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
    }
}
