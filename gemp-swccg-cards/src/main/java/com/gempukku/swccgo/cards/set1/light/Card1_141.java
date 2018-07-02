package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold 1
 */
public class Card1_141 extends AbstractStarfighter {
    public Card1_141() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 3, "Gold 1", Uniqueness.UNIQUE);
        setLore("Lead fighter of Gold Squadron at Battle of Yavin. Flown by Jon 'Dutch' Vander. Designated Specter 1 at Renforra Base.");
        setGameText("May add 2 pilots or passengers. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.GOLD_1);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(2);
    }
}
