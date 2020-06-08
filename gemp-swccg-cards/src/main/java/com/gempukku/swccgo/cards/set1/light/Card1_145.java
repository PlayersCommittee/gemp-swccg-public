package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 3
 */
public class Card1_145 extends AbstractStarfighter {
    public Card1_145() {
        super(Side.LIGHT, 3, 2, 3, null, 4, 5, 5, Title.Red_3, Uniqueness.UNIQUE);
        setLore("Biggs Darklighter's fighter at Battle of Yavin. Part of Ecliptic Evaders squadron near Sullust until transferred to Yavin Base.");
        setGameText("May add 1 pilot and 1 astromech. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
    }
}
