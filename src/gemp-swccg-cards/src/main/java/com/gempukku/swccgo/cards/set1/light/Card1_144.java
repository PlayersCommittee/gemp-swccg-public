package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
 * Title: Red 1
 */
public class Card1_144 extends AbstractStarfighter {
    public Card1_144() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, "Red 1", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Lead fighter of Red Squadron at Battle of Yavin. Flown by Garven Dreis. Also served at main Rebel base on Dantooine.");
        setGameText("May add 1 pilot. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.RED_1);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
    }
}
