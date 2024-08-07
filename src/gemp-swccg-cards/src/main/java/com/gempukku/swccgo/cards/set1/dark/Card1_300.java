package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 3
 */
public class Card1_300 extends AbstractStarfighter {
    public Card1_300() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, "Black 3", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("TIE/ln fighter of pilot DS-61-3. Stylized image of Corellian slice-hound painted on inner hatch.");
        setGameText("May add 1 pilot. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.BLACK_3);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
    }
}
