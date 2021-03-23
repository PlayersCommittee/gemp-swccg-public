package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Alien
 * Title: Heavy Infantry Mandalorian
 */
public class Card501_010 extends AbstractAlien {
    public Card501_010() {
        super(Side.LIGHT, 3, 3, 3, 2, 4, "Heavy Infantry Mandalorian", Uniqueness.RESTRICTED_3);
        setLore("");
        setGameText("Permanent weapon is Flame Thrower (may target a character for free; draw destiny; target hit if destiny +1 > defense value. May 'fly' (landspeed = 3). May move as a 'react'.");
        setArmor(4);
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_15);
        setTestingText("Heavy Infantry Mandalorian");
        hideFromDeckBuilder();
    }
}