package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;

/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used
 * Title: For the Republic!
 */
public class Card501_012 extends AbstractUsedInterrupt {
    public Card501_012() {
        super(Side.DARK, 5, "For the Republic!");
        setLore("");
        setGameText("If a battle was just initiated at a site, each of your clones present is power +1 (power +2 with a Jedi) and immune to attrition for the remainder of turn. OR deploy Cloning Cylnders from hand or deck (for free); reshuffle.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_15);
        setTestingText("For the Republic!");
    }
}
