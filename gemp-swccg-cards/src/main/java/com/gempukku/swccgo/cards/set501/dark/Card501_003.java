package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used
 * Title: Projective Telepathy (V)
 */
public class Card501_003 extends AbstractUsedInterrupt {
    public Card501_003() {
        super(Side.DARK, 3, "Projective Telepathy", Uniqueness.UNIQUE);
        setLore("'Luke.' 'Father.' 'Son, come with me.'");
        setGameText("If drawn for destiny, may be taken into hand.During opponent's control phase target a location. Opponent must have total ability of >6 to draw battle destiny there until end of the turn. OR Draw a card from your force pile.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Projective Telepathy (V)");
    }
}
