package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold 5
 */
public class Card1_142 extends AbstractStarfighter {
    public Card1_142() {
        super(Side.LIGHT, 4, 1, 2, null, 3, 4, 3, Title.Gold_5, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Flown by the veteran Rebel fighter pilot Pops during the Battle of Yavin. He personally supervised all repairs and maintenance.");
        setGameText("May add 2 pilots or passengers, and 1 astromech. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(2);
        setAstromechCapacity(1);
    }
}
